package ru.netology.nework.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.netology.nework.entity.EventEntity
import ru.netology.nework.entity.EventRemoteKeyEntity
import ru.netology.nework.dao.*


@Database(
    entities = [EventEntity::class, EventRemoteKeyEntity::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(
    Converters::class, CoordinatesConverter::class, EventTypeConverters::class,
    ConvertersListIds::class
)
abstract class EventAppDb : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun eventRemoteKeyDao(): EventRemoteKeyDao
}