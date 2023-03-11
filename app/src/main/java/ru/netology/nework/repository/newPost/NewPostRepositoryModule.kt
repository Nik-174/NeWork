package ru.netology.nework.repository.newPost

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.nework.repository.newPost.NewPostRepository
import ru.netology.nework.repository.newPost.NewPostRepositoryImpl

import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface NewPostRepositoryModule {
    @Binds
    @Singleton
    fun bindListOfUserRepository(impl: NewPostRepositoryImpl): NewPostRepository
}