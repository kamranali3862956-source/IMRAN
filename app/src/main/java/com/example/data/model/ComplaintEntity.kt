package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "complaints")
data class ComplaintEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val trackingCode: String,
    val userId: Long,
    val userName: String,
    val userPhone: String,
    val userArea: String,
    val category: String, // WATER, ELECTRICITY, SANITATION, ROADS, HEALTH, EDUCATION, OTHER
    val title: String,
    val description: String,
    val locationDetails: String,
    val priority: String = "MEDIUM", // LOW, MEDIUM, HIGH, EMERGENCY
    val status: String = "PENDING", // PENDING, IN_PROGRESS, RESOLVED, REJECTED
    val photoUri: String? = null,
    val adminRemarks: String = "",
    val assignedTo: String = "Youth Committee Desk",
    val citizenRating: Int = 0, // 0 = unrated, 1 to 5
    val citizenFeedback: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
