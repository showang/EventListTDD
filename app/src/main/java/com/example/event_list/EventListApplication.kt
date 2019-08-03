package com.example.event_list

import android.app.Application
import com.example.event_list.db.EventRepository
import com.example.event_list.db.EventRoomDatabase
import com.example.event_list.presenter.EventListPresenter
import com.example.event_list.presenter.EventPresenter
import org.koin.core.context.startKoin
import org.koin.dsl.module

class EventListApplication : Application() {

    private val appModule = module {
        single { EventRoomDatabase.getDatabase(applicationContext).eventDao() }
        single { EventRepository(get()) }
        single<EventPresenter> { EventListPresenter(get()) }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(appModule)
        }
    }
}