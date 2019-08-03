package com.example.event_list.presenter

import com.example.event_list.db.EventRepository
import com.example.event_list.model.Category
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

    companion object {
        private val defaultCategories = arrayOf("Person", "Business", "Others")
    }


    init {
        dbScope.launch {
            cachedEventList.addAll(repository.getAllEvents())
            cachedCategory.addAll(repository.getCategories())
            if (cachedCategory.isEmpty()) {
                defaultCategories.forEach {
                    Category(it).run {
                        cachedCategory.add(this)
                        repository.addCategory(this)
                    }
                }
            }
            mInitializing = false
            withContext(uiDispatcher) {
                eventListView?.onEventsUpdate()
            }
        }
    }


    override var eventListView: EventListView? = null

    override val initializing get() = mInitializing
    override val eventList: List<EventItem> get() = cachedEventList
    override val categories: List<Category> get() = cachedCategory

    private var cachedCategory: MutableList<Category> = mutableListOf()
    private var cachedEventList: MutableList<EventItem> = mutableListOf()
    private var mInitializing = true

//    suspend fun init() {
//        cachedEventList.addAll(repository.getAllEvents())
//        cachedCategory.addAll(repository.getCategories())
//        if (cachedCategory.isEmpty()) {
//            defaultCategories.forEach {
//                Category(it).run {
//                    cachedCategory.add(this)
//                    repository.addCategory(this)
//                }
//            }
//        }
//        mInitializing = false
//        withContext(uiDispatcher) {
//            eventListView?.onEventsUpdate()
//        }
//    }

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
            index = when {
                to >= cachedEventList.size - 1 -> cachedEventList.last().index + 1.0
                to > 0 -> {
                    val toItem = cachedEventList[to]
                    val nextItem = cachedEventList[if (to > from) (to + 1) else (to - 1)]
                    (toItem.index + nextItem.index) / 2
                }
                else -> if (cachedEventList.isEmpty()) {
                    1.0
                } else {
                    cachedEventList.first().index / 2
                }
            }
        }
        Collections.swap(cachedEventList, from, to)
        dbScope.launch {
            repository.insert(fromEvent)
        }
        eventListView?.onEventItemSwapped(from, to)
    }

    override fun addCategory(category: Category) {
        cachedCategory.add(category)
        dbScope.launch {
            repository.addCategory(category)
        }
    }

    override fun delete(index: Int) {
        val item = cachedEventList.removeAt(index)
        dbScope.launch {
            repository.deleteItem(item.id)
        }
        eventListView?.onEventDelete(index, item)
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

    fun onEventDelete(atIndex: Int, item: EventItem)

}

interface EventPresenter {

    val initializing: Boolean

    var eventListView: EventListView?

    val eventList: List<EventItem>

    val categories: List<Category>

    fun addCategory(category: Category)

    fun append(event: EventItem)

    fun update(event: EventItem)

    fun swapItem(from: Int, to: Int)

    fun delete(index: Int)

    fun clearEvents()
}