package com.example.event_list.db

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.event_list.model.EventItem

class EventRepository(private val eventDao: EventDao) {

    val allEventLiveData: LiveData<List<EventItem>> = eventDao.getAllEventLiveData()

    @WorkerThread
    suspend fun getAllEvents(): List<EventItem> = eventDao.getAllEvents()

    @WorkerThread
    suspend fun insert(event: EventItem) = eventDao.insert(event)

//    @WorkerThread
//    suspend fun getEventById(id: String): EventItem? = eventDao.getEventById(id)
//
//    @WorkerThread
//    suspend fun insertAtIndex(i: Int, event: EventItem) {
//        val events = allEventLiveData.value ?: return
//        eventDao.insert(event.apply {
//            index = when {
//                i >= events.size -> events.last().index + 1.0
//                i > 0 -> (events[i].index + events[i + 1].index) / 2
//                else -> 0.0
//            }
//        })
//    }
}