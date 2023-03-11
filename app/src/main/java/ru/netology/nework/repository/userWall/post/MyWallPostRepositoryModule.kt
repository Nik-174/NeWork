package ru.netology.nework.repository.userWall.post

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.nework.repository.userWall.post.MyWallPostRepository
import ru.netology.nework.repository.userWall.post.MyWallPostRepositoryImpl
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface MyWallPostRepositoryModule {
    @Binds
    @Singleton
    fun bindMyWallPostRepository(impl: MyWallPostRepositoryImpl): MyWallPostRepository
}