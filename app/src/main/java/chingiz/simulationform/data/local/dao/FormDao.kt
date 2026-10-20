package chingiz.simulationform.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import chingiz.simulationform.data.local.entity.FormItemEntity
import chingiz.simulationform.data.local.entity.ResponseEntity
import chingiz.simulationform.data.local.entity.ResponseSetEntity
import chingiz.simulationform.data.local.entity.SelectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FormDao {

    @Query("SELECT * FROM form_items ORDER BY position")
    fun observeItems(): Flow<List<FormItemEntity>>

    @Query("SELECT * FROM response_sets")
    fun observeResponseSets(): Flow<List<ResponseSetEntity>>

    @Query("SELECT * FROM responses ORDER BY position")
    fun observeResponses(): Flow<List<ResponseEntity>>

    @Query("SELECT * FROM form_items WHERE id = :id AND type = :type LIMIT 1")
    suspend fun getItem(id: Long, type: String): FormItemEntity?

    @Query("DELETE FROM form_items")
    suspend fun clearItems()

    @Insert
    suspend fun insertItems(items: List<FormItemEntity>)

    @Insert
    suspend fun insertResponseSets(sets: List<ResponseSetEntity>)

    @Insert
    suspend fun insertResponses(responses: List<ResponseEntity>)

    /** Atomically replaces the cached form; response sets/responses are removed via cascade. */
    @Transaction
    suspend fun replaceForm(
        items: List<FormItemEntity>,
        responseSets: List<ResponseSetEntity>,
        responses: List<ResponseEntity>,
    ) {
        clearItems()
        insertItems(items)
        insertResponseSets(responseSets)
        insertResponses(responses)
    }

    @Query("SELECT * FROM selections")
    fun observeSelections(): Flow<List<SelectionEntity>>

    @Query("DELETE FROM selections WHERE question_id = :questionId")
    suspend fun clearSelection(questionId: Long)

    @Insert
    suspend fun insertSelections(selections: List<SelectionEntity>)

    @Transaction
    suspend fun replaceSelection(questionId: Long, responseIds: Set<Long>) {
        clearSelection(questionId)
        insertSelections(responseIds.map { SelectionEntity(questionId, it) })
    }
}
