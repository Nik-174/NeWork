package ru.netology.nework.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.netology.nework.dao.Converters
import ru.netology.nework.dao.JobDao
import ru.netology.nework.entity.JobEntity


@Database(entities = [JobEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class JobAppDb : RoomDatabase() {
    abstract fun jobDao(): JobDao
}