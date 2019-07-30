package com.example.event_list

import com.example.event_list.model.EventCategory
import com.example.event_list.model.EventItem
import com.example.event_list.model.EventManager
import org.junit.Test

class EventManagerTest {

    @Test
    fun testAddEvent() {
        val itemList = mutableListOf<EventItem>()
        val manager = EventManager(itemList)
        manager.addEvent(EventItem("title", "description", 0, 0, EventCategory()))
    }

    @Test
    fun testRemoveEvent() {
        val itemList = mutableListOf<EventItem>()
        val manager = EventManager(itemList)
        manager.addEvent(EventItem("title", "description", 0, 0, EventCategory()))

        assert(manager.removeEvent(0)){"remove event failed"}
        assert(!manager.removeEvent(0)){"remove on empty list must be failed"}
    }

    @Test
    fun testEditEvent() {
        val itemList = mutableListOf<EventItem>()
        val manager = EventManager(itemList)
        manager.addEvent(EventItem("title", "description", 0, 0, EventCategory()))

        manager.events[0]
    }

}