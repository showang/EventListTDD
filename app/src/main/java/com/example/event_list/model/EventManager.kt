package com.example.event_list.model

class EventManager(private val itemList: MutableList<EventItem>) {

    val events: List<EventItem> get() = itemList

    fun addEvent(item: EventItem) {
        itemList.add(0, item)
    }

    fun removeEvent(index: Int): Boolean {
        return try {
            itemList.removeAt(index)
            true
        } catch (e: Throwable) {
            false
        }
    }

    fun command() {

    }
}