package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "announcements")
data class AnnouncementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val category: String, // WATER_SUPPLY, CLEANLINESS, POWER, NOTICE
    val date: Long = System.currentTimeMillis(),
    val author: String = "Chashma Goth Youth Generation"
)
