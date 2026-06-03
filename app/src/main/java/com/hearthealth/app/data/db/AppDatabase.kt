package com.hearthealth.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hearthealth.app.data.dao.JournalDao
import com.hearthealth.app.data.dao.LifestyleDao
import com.hearthealth.app.data.dao.MedicationDao
import com.hearthealth.app.data.dao.ReminderDao
import com.hearthealth.app.data.entity.JournalEntry
import com.hearthealth.app.data.entity.LifestyleData
import com.hearthealth.app.data.entity.Medication
import com.hearthealth.app.data.entity.Reminder

@Database(
    entities = [
        Medication::class,
        Reminder::class,
        JournalEntry::class,
        LifestyleData::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun medicationDao(): MedicationDao
    abstract fun reminderDao(): ReminderDao
    abstract fun journalDao(): JournalDao
    abstract fun lifestyleDao(): LifestyleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hearthealth_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
