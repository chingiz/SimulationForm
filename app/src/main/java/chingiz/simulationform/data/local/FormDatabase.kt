package chingiz.simulationform.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import chingiz.simulationform.data.local.dao.FormDao
import chingiz.simulationform.data.local.entity.FormItemEntity
import chingiz.simulationform.data.local.entity.ResponseEntity
import chingiz.simulationform.data.local.entity.ResponseSetEntity
import chingiz.simulationform.data.local.entity.SelectionEntity

@Database(
    entities = [
        FormItemEntity::class,
        ResponseSetEntity::class,
        ResponseEntity::class,
        SelectionEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class FormDatabase : RoomDatabase() {
    abstract fun formDao(): FormDao

    companion object {
        const val NAME = "simulation_form.db"
    }
}
