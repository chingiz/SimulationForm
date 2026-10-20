package chingiz.simulationform.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "response_sets",
    foreignKeys = [
        ForeignKey(
            entity = FormItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["question_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("question_id", unique = true)],
)
data class ResponseSetEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "question_id") val questionId: Long,
    @ColumnInfo(name = "multiple_selection") val multipleSelection: Boolean,
)
