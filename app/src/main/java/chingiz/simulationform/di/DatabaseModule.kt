package chingiz.simulationform.di

import android.content.Context
import androidx.room.Room
import chingiz.simulationform.data.local.FormDatabase
import chingiz.simulationform.data.local.dao.FormDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FormDatabase =
        Room.databaseBuilder(context, FormDatabase::class.java, FormDatabase.NAME).build()

    @Provides
    fun provideFormDao(database: FormDatabase): FormDao = database.formDao()
}
