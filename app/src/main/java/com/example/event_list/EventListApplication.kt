package com.example.event_list

import android.app.Application
import com.example.event_list.db.EventRepository
import com.example.event_list.db.EventRoomDatabase
import com.example.event_list.presenter.EventListPresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

class EventListApplication : Application() {


    private val appModule = module {
        single { EventRoomDatabase.getDatabase(applicationContext).eventDao() }
        single { EventRepository(get()) }
        single { EventListPresenter(get(), get(named("db_scope"))) }
        single(named("db_scope")) { CoroutineScope(IO) }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(appModule)
        }
    }
}