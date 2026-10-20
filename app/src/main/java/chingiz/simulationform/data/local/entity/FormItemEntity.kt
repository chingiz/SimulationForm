package chingiz.simulationform.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Adjacency-list representation of the form tree: every node references its parent,
 * pages have a null [parentId]. [position] keeps the original ordering among siblings.
 */
@Entity(
    tableName = "form_items",
    foreignKeys = [
        ForeignKey(
            entity = FormItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["parent_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("parent_id")],
)
data class FormItemEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "parent_id") val parentId: Long?,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "position") val position: Int,
    @ColumnInfo(name = "title") val title: String? = null,
    @ColumnInfo(name = "content") val content: String? = null,
    @ColumnInfo(name = "src") val src: String? = null,
)
