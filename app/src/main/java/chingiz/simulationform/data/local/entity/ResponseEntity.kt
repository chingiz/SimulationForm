package chingiz.simulationform.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "responses",
    foreignKeys = [
        ForeignKey(
            entity = ResponseSetEntity::class,
            parentColumns = ["id"],
            childColumns = ["response_set_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("response_set_id")],
)
data class ResponseEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "response_set_id") val responseSetId: Long,
    @ColumnInfo(name = "label") val label: String,
    @ColumnInfo(name = "score") val score: Int?,
    @ColumnInfo(name = "position") val position: Int,
)
