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
    private val dbScope: CoroutineScope = CoroutineScope(IO),
    private val uiDispatcher: CoroutineContext = Main
) : EventPresenter {

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
        val fromEvent = cachedEventList[from]
        Collections.swap(cachedEventList, from, to)
        dbScope.launch {
            repository.insert(fromEvent.apply {
                index = when {
                    to >= cachedEventList.size - 1 -> cachedEventList.last().index + 1.0
                    to > 0 -> (cachedEventList[to].index + cachedEventList[if (to > index) to + 1 else to - 1].index) / 2
                    else -> if (cachedEventList.isEmpty()) {
                        1.0
                    } else {
                        cachedEventList.first().index / 2
                    }
                }
            })
        }
        eventListView?.onEventItemSwapped(from, to)
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
}