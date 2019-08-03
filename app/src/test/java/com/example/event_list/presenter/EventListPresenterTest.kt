package com.example.event_list.presenter

import com.example.event_list.db.EventRepository
import com.example.event_list.model.Category
import com.example.event_list.model.EventItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import java.util.*

class EventListPresenterTest {

    lateinit var repository: EventRepository
    lateinit var presenter: EventPresenter
    lateinit var mockView: EventListView

    private val mockEvent get() = EventItem("mock", "mockTitle", "mockDesc", Date(), Date(), Category("mock category"))

    @Before
    fun setup() {
        repository = mock(EventRepository::class.java)
        mockView = mock(EventListView::class.java)
        runBlocking {
            `when`(repository.getAllEvents()).thenReturn(mutableListOf())
            presenter = EventListPresenter(repository, this, Dispatchers.Default).apply {
                eventListView = mockView
            }
        }
    }

    @Test
    fun testCreate() {
        presenter.append(mockEvent)

        verify(mockView).onEventInsert(presenter.eventList.size - 1)
        assert(presenter.eventList.size == 1)
    }

    @Test
    fun testUpdate() {
        presenter.append(mockEvent)
        presenter.update(presenter.eventList.first().apply {
            title = "update title"
            desc = "update desc"
        })

        verify(mockView).onEventItemUpdate(0)
        presenter.eventList[0].apply {
            assert(title == "update title")
            assert(desc == "update desc")
        }
    }

    @Test
    fun testSwap() {
        runBlocking {
            presenter.append(mockEvent.apply {
                title = "event 1"
            })
            delay(100)
            presenter.append(mockEvent.apply {
                title = "event 2"
            })
            delay(100)
            presenter.append(mockEvent.apply {
                title = "event 3"
            })
        }

        presenter.swapItem(0, 2)

        verify(mockView).onEventItemSwapped(0, 2)

        assert(presenter.eventList[0].title == "event 3")
        assert(presenter.eventList[2].title == "event 1")
    }
}