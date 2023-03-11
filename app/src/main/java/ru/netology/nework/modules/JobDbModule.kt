package ru.netology.nework.modules

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.netology.nework.dao.JobDao
import ru.netology.nework.db.JobAppDb
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class JobDbModule {

    @Singleton
    @Provides
    fun provideAppDb(
        @ApplicationContext context: Context
    ): JobAppDb = Room.databaseBuilder(context, JobAppDb::class.java, "jobs.db")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun provideJobDao(appDb: JobAppDb): JobDao = appDb.jobDao()
}