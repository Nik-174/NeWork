package ru.netology.nework.modules

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.netology.nework.dao.PostDao
import ru.netology.nework.dao.PostRemoteKeyDao
import ru.netology.nework.db.PostAppDb
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class PostDbModule {

    @Singleton
    @Provides
    fun provideAppDb(
        @ApplicationContext context: Context
    ): PostAppDb = Room.databaseBuilder(context, PostAppDb::class.java, "posts.db")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun providePostDao(appDb: PostAppDb): PostDao = appDb.postDao()

    @Provides
    fun providePostRemoteKeyDao(appDb: PostAppDb): PostRemoteKeyDao = appDb.postRemoteKeyDao()
}