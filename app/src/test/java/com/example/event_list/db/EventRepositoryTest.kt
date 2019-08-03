package com.example.event_list.db

import com.example.event_list.model.Category
import com.example.event_list.model.EventItem
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import java.util.*

class EventRepositoryTest {

    private lateinit var mockDao: EventDao
    private lateinit var repository: EventRepository

    private val mockEvent get() = EventItem("mock", "mockTitle", "mockDesc", Date(), Date(), Category("mock category"))

    @Before
    fun setup() {
        mockDao = mock(EventDao::class.java)
        repository = EventRepository(mockDao)
    }

    @Test
    fun testInsert() {
        val event = mockEvent
        runBlocking {
            repository.insert(event)
            verify(mockDao).insert(event)
        }
    }

    @Test
    fun testGetAllEvents() {
        val initList = (0..10).map { mockEvent }.toList()
        runBlocking {
            `when`(mockDao.getAllEvents()).thenReturn(initList)
            val events = repository.getAllEvents()
            verify(mockDao, times(1)).getAllEvents()
            assert(events == initList)
        }
    }

    @Test
    fun testClearEvents() {
        runBlocking {
            repository.clearEvents()
            verify(mockDao, times(1)).deleteAll()
        }
    }

}