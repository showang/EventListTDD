package com.example.event_list

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.event_list.db.EventDao
import com.example.event_list.db.EventRoomDatabase
import com.example.event_list.model.EventItem
import com.example.event_list.viewmodel.EventViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class EventViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var dao: EventDao
    private lateinit var db: EventRoomDatabase

    private lateinit var viewModel: EventViewModel

    @Before
    fun createDb() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        db = Room.inMemoryDatabaseBuilder(app, EventRoomDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.eventDao()
        viewModel = EventViewModel(app)
    }

    @Test
    fun testInsert() {
        viewModel.insert(EventItem("4", "title", "desc", Date(), Date()))

        assert(viewModel.allEvent.value?.size == 1)
    }
}