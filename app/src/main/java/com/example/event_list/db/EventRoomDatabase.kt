package com.example.event_list.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.event_list.model.Category
import com.example.event_list.model.EventItem

@Database(entities = [EventItem::class, Category::class], version = 1)
abstract class EventRoomDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao

    companion object {
        @Volatile
        private var INSTANCE: EventRoomDatabase? = null

        fun getDatabase(context: Context): EventRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    EventRoomDatabase::class.java,
                    "event_database"
                ).build().apply { INSTANCE = this }
            }
        }
    }
}

