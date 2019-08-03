package com.example.event_list.db

import androidx.annotation.WorkerThread
import com.example.event_list.model.Category
import com.example.event_list.model.EventItem

class EventRepository(private val eventDao: EventDao) {

    @WorkerThread
    suspend fun getAllEvents(): List<EventItem> = eventDao.getAllEvents()

    @WorkerThread
    suspend fun insert(event: EventItem) = eventDao.insert(event)

    @WorkerThread
    suspend fun clearEvents() = eventDao.deleteAll()

    @WorkerThread
    suspend fun deleteItem(id: String) = eventDao.delete(id)

    @WorkerThread
    suspend fun getCategories() = eventDao.categories()

    @WorkerThread
    suspend fun addCategory(category: Category) = eventDao.addCategory(category)
}