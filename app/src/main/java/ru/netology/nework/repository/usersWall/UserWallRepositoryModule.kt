package ru.netology.nework.repository.usersWall

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.nework.repository.usersWall.UserWallRepository
import ru.netology.nework.repository.usersWall.UserWallRepositoryImpl
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface UserWallRepositoryModule {
    @Binds
    @Singleton
    fun bindUserWallRepository(impl: UserWallRepositoryImpl): UserWallRepository
}