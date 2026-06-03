package com.hearthealth.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val feelings: String,
    val symptoms: String,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
