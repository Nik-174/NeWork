package ru.netology.nework.modules

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.netology.nework.dao.MyWallPostDao
import ru.netology.nework.dao.MyWallRemoteKeyDao
import ru.netology.nework.db.WallPostAppDb
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class WallDbModule {
    @Singleton
    @Provides
    fun provideAppDb(
        @ApplicationContext context: Context
    ): WallPostAppDb = Room.databaseBuilder(context, WallPostAppDb::class.java, "wallPosts.db")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun provideWallPostAppDb(appDb: WallPostAppDb): MyWallPostDao = appDb.myWallPostDao()

    @Provides
    fun provideWallPostRemoteKeyDao(appDb: WallPostAppDb): MyWallRemoteKeyDao =
        appDb.myWallPostRemoteKeyDao()
}