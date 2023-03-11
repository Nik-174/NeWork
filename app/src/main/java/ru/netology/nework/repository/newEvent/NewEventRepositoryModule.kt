package ru.netology.nework.repository.newEvent

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.nework.repository.newEvent.NewEventRepository
import ru.netology.nework.repository.newEvent.NewEventRepositoryImpl

import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface NewEventRepositoryModule {
    @Binds
    @Singleton
    fun bindNewEventRepository(impl: NewEventRepositoryImpl): NewEventRepository
}