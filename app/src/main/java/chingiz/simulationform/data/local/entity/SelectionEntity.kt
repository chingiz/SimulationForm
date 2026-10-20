package chingiz.simulationform.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

/**
 * User answers. Intentionally not bound by foreign keys so selections survive a refresh
 * that replaces the cached form content.
 */
@Entity(tableName = "selections", primaryKeys = ["question_id", "response_id"])
data class SelectionEntity(
    @ColumnInfo(name = "question_id") val questionId: Long,
    @ColumnInfo(name = "response_id") val responseId: Long,
)
