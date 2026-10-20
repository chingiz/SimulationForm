package chingiz.simulationform.domain.repository

import chingiz.simulationform.domain.model.DataResult
import chingiz.simulationform.domain.model.ImageQuestion
import chingiz.simulationform.domain.model.Page
import kotlinx.coroutines.flow.Flow

interface FormRepository {

    /** Emits the locally cached form. The local database is the single source of truth. */
    fun observePages(): Flow<List<Page>>

    /** Fetches the form from the network and replaces the cached copy on success. */
    suspend fun refresh(): DataResult<Unit>

    suspend fun getImageQuestion(id: Long): ImageQuestion?

    /** Map of choice question id to the ids of its selected responses. */
    fun observeSelections(): Flow<Map<Long, Set<Long>>>

    suspend fun saveSelection(questionId: Long, responseIds: Set<Long>)
}
