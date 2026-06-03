package com.hearthealth.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lifestyle_data")
data class LifestyleData(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val exerciseMinutes: Int,
    val heartRate: Int,
    val stepCount: Int = 0,
    val date: Long = System.currentTimeMillis()
)
