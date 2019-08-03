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
    private val once = times(1)

    @Before
    fun setup() {
        repository = mock(EventRepository::class.java)
        mockView = mock(EventListView::class.java)
        runBlocking {
            `when`(repository.getAllEvents()).thenReturn(mutableListOf())
            presenter = EventListPresenter(repository, Dispatchers.Default, Dispatchers.Default).apply {
                eventListView = mockView
            }
        }
    }

    @Test
    fun testCreate() {
        presenter.append(mockEvent)

        verify(mockView, once).onEventInsert(presenter.eventList.size - 1)
        assert(presenter.eventList.size == 1)
    }

    @Test
    fun testCreate_notEmpty() {
        runBlocking {
            `when`(repository.getAllEvents()).thenReturn(mutableListOf(mockEvent))
            presenter = EventListPresenter(repository, Dispatchers.Default, Dispatchers.Default).apply {
                eventListView = mockView
            }
        }

        presenter.append(mockEvent)
        presenter.append(mockEvent)
        verify(mockView, once).onEventInsert(1)
        verify(mockView, once).onEventInsert(2)
        assert(presenter.eventList.size == 3)
    }

    @Test
    fun testUpdate() {
        presenter.append(mockEvent)
        presenter.update(presenter.eventList.first().apply {
            title = "update title"
            desc = "update desc"
        })

        verify(mockView, once).onEventItemUpdate(0)
        presenter.eventList[0].apply {
            assert(title == "update title")
            assert(desc == "update desc")
        }
    }

    @Test
    fun testSwap_frontToBack() {
        runBlocking {
            for (i in 1..3) {
                presenter.append(mockEvent.apply {
                    title = "event $i"
                })
                delay(100)
            }
        }

        presenter.swapItem(0, 2)

        verify(mockView, once).onEventItemSwapped(0, 2)

        assert(presenter.eventList[0].title == "event 3")
        assert(presenter.eventList[2].title == "event 1")
    }

    @Test
    fun testSwap_backToFront() {
        runBlocking {
            for (i in 1..3) {
                presenter.append(mockEvent.apply {
                    title = "event $i"
                })
                delay(100)
            }
        }

        presenter.swapItem(2, 0)

        verify(mockView, once).onEventItemSwapped(2, 0)

        assert(presenter.eventList[0].title == "event 3")
        assert(presenter.eventList[2].title == "event 1")
    }

    @Test
    fun testSwap_middle() {
        runBlocking {
            for (i in 1..10) {
                presenter.append(mockEvent.apply {
                    title = "event $i"
                })
                delay(30)
            }
            presenter.swapItem(2, 4)

            verify(mockView, once).onEventItemSwapped(2, 4)

            assert(presenter.eventList[2].title == "event 5")
            assert(presenter.eventList[4].index == 5.5)
            assert(presenter.eventList[4].title == "event 3")
        }


    }

    @Test
    fun testClearEvents() {
        runBlocking {
            for (i in 1..10) {
                presenter.append(mockEvent.apply {
                    title = "event $i"
                })
                delay(30)
            }
        }

        presenter.clearEvents()

        verify(mockView, times(2)).onEventsUpdate()
        assert(presenter.eventList.isEmpty())
    }
}