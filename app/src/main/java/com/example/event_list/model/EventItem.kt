package com.example.event_list.model

import androidx.room.*
import java.util.*

@Entity(tableName = "event_table")
@TypeConverters(DateConverters::class)
class EventItem(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "desc") var desc: String,
    @ColumnInfo(name = "start_date") var startDate: Date,
    @ColumnInfo(name = "end_date") var endDate: Date,
    @Embedded var category: Category,
    var index: Double = 0.0
)

class DateConverters {

    @TypeConverter
    fun dateFromTimestamp(ms: Long?): Date? = ms?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time
}