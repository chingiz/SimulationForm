package chingiz.simulationform.data.repository

import chingiz.simulationform.data.local.dao.FormDao
import chingiz.simulationform.data.mapper.FormEntities
import chingiz.simulationform.data.mapper.toEntities
import chingiz.simulationform.data.mapper.toImageQuestion
import chingiz.simulationform.data.mapper.toPages
import chingiz.simulationform.data.remote.api.FormApi
import chingiz.simulationform.data.remote.dto.FormItemType
import chingiz.simulationform.data.remote.retryWithBackoff
import chingiz.simulationform.data.remote.safeApiCall
import chingiz.simulationform.di.IoDispatcher
import chingiz.simulationform.domain.model.DataResult
import chingiz.simulationform.domain.model.ImageQuestion
import chingiz.simulationform.domain.model.Page
import chingiz.simulationform.domain.repository.FormRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Offline-first repository: the UI always reads from Room, the network only writes into it.
 * A failed refresh therefore never wipes previously fetched data.
 */
@Singleton
class FormRepositoryImpl @Inject constructor(
    private val api: FormApi,
    private val dao: FormDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : FormRepository {

    override fun observePages(): Flow<List<Page>> =
        combine(dao.observeItems(), dao.observeResponseSets(), dao.observeResponses()) { items, sets, responses ->
            FormEntities(items, sets, responses).toPages()
        }
            .distinctUntilChanged()
            .flowOn(ioDispatcher)

    override suspend fun refresh(): DataResult<Unit> = withContext(ioDispatcher) {
        when (val result = retryWithBackoff { safeApiCall { api.getForm() } }) {
            is DataResult.Success -> {
                val entities = result.data.toPages().toEntities()
                dao.replaceForm(entities.items, entities.responseSets, entities.responses)
                DataResult.Success(Unit)
            }
            is DataResult.Error -> result
        }
    }

    override suspend fun getImageQuestion(id: Long): ImageQuestion? = withContext(ioDispatcher) {
        dao.getItem(id, FormItemType.IMAGE)?.toImageQuestion()
    }

    override fun observeSelections(): Flow<Map<Long, Set<Long>>> =
        dao.observeSelections()
            .map { rows -> rows.groupBy({ it.questionId }, { it.responseId }).mapValues { it.value.toSet() } }
            .flowOn(ioDispatcher)

    override suspend fun saveSelection(questionId: Long, responseIds: Set<Long>) =
        withContext(ioDispatcher) { dao.replaceSelection(questionId, responseIds) }
}
