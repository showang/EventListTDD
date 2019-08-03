package com.example.event_list.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.event_list.model.Category
import com.example.event_list.model.EventItem

@Dao
interface EventDao {

//    @Query("SELECT * from event_table ORDER BY `index`")
//    fun getAllEventLiveData(): LiveData<List<EventItem>>

    @Query("SELECT * FROM event_table ORDER BY `index`")
    suspend fun getAllEvents(): List<EventItem>

//    @Query("SELECT * from event_table WHERE id = :id")
//    suspend fun getEventById(id: String): EventItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventItem)

    @Query("DELETE FROM event_table")
    suspend fun deleteAll()

    @Query("DELETE FROM event_table WHERE id = :eventId")
    suspend fun delete(eventId: String)

    @Query("SELECT * FROM category_table")
    suspend fun categories(): List<Category>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCategory(category: Category)
}