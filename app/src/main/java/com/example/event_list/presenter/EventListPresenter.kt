package com.example.event_list.presenter

import com.example.event_list.db.EventRepository
import com.example.event_list.model.EventItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.coroutines.CoroutineContext

class EventListPresenter(
    private val repository: EventRepository,
    private val dbDispatcher: CoroutineContext = IO,
    private val uiDispatcher: CoroutineContext = Main
) : EventPresenter {

    private val dbScope = CoroutineScope(dbDispatcher)

    init {
        dbScope.launch {
            cachedEventList.addAll(repository.getAllEvents())
            mInitializing = false
            withContext(uiDispatcher) {
                eventListView?.onEventsUpdate()
            }
        }
    }

    override var eventListView: EventListView? = null

    override val initializing get() = mInitializing
    override val eventList: List<EventItem> get() = cachedEventList

    private var cachedEventList: MutableList<EventItem> = mutableListOf()
    private var mInitializing = true


    override fun append(event: EventItem) {
        event.apply {
            index = if (cachedEventList.isEmpty()) {
                1.0
            } else {
                cachedEventList.last().index + 1.0
            }
        }
        cachedEventList.add(event)
        dbScope.launch { repository.insert(event) }
        eventListView?.onEventInsert(cachedEventList.size - 1)
    }

    override fun update(event: EventItem) {
        dbScope.launch { repository.insert(event) }
        eventListView?.onEventItemUpdate(cachedEventList.indexOf(event))
    }

    override fun swapItem(from: Int, to: Int) {
        val fromEvent = cachedEventList[from].apply {
            println("swap to index $to before $index")
            index = when {
                to >= cachedEventList.size - 1 -> cachedEventList.last().index + 1.0
                to > 0 -> {
                    val toItem = cachedEventList[to]
                    val nextItem = cachedEventList[if (to > from) (to + 1) else (to - 1)]
                    println("toItem index: ${toItem.index}")
                    println("nextItem index: ${nextItem.index}")
                    (toItem.index + nextItem.index) / 2
                }
                else -> if (cachedEventList.isEmpty()) {
                    1.0
                } else {
                    cachedEventList.first().index / 2
                }
            }

            println("swap index before $index")
        }
        Collections.swap(cachedEventList, from, to)
        dbScope.launch {
            repository.insert(fromEvent)
        }
        eventListView?.onEventItemSwapped(from, to)
    }

    override fun clearEvents() {
        cachedEventList.clear()
        dbScope.launch {
            repository.clearEvents()
        }
        eventListView?.onEventsUpdate()
    }
}

interface EventListView {

    fun onEventsUpdate()

    fun onEventItemSwapped(from: Int, to: Int)

    fun onEventInsert(atIndex: Int)

    fun onEventItemUpdate(atIndex: Int)

}

interface EventPresenter {

    val initializing: Boolean

    var eventListView: EventListView?

    val eventList: List<EventItem>

    fun append(event: EventItem)

    fun update(event: EventItem)

    fun swapItem(from: Int, to: Int)

    fun clearEvents()
}