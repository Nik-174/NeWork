package ru.netology.nework.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.google.gson.Gson
import ru.netology.nework.dto.ListIds
import ru.netology.nework.entity.EventEntity
import ru.netology.nework.enumeration.EventType

@Dao
interface EventDao {
    @Query("SELECT * FROM EventEntity ORDER BY id DESC")
    fun getAll(): PagingSource<Int, EventEntity>

    @Query("SELECT COUNT(*) == 0 FROM EventEntity")
    suspend fun isEmpty(): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(events: List<EventEntity>)

    @Query("DELETE FROM EventEntity WHERE id = :id")
    suspend fun removeById(id: Int)

    @Query("SELECT COUNT() FROM EventEntity")
    suspend fun isSize(): Int

    @Query("DELETE FROM EventEntity")
    suspend fun removeAll()
}

class EventTypeConverters {
    @TypeConverter
    fun toTypeEvent(value: String) = enumValueOf<EventType>(value)

    @TypeConverter
    fun fromTypeEvent(value: EventType) = value.name
}

class ConvertersListIds {
    @TypeConverter
    fun fromListIds(listIds: ListIds): String {
        return Gson().toJson(listIds)
    }

    @TypeConverter
    fun toListIds(sh: String): ListIds {
        return Gson().fromJson(sh, ListIds::class.java)
    }
}