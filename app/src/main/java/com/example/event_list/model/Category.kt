package com.example.event_list.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "category_table")
class Category(@PrimaryKey val name: String)