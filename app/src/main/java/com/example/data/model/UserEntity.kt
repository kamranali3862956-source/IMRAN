package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phone: String,
    val email: String = "",
    val password: String,
    val area: String = "Fishermen Colony",
    val role: String = "RESIDENT", // "RESIDENT" or "ADMIN"
    val createdAt: Long = System.currentTimeMillis()
)
