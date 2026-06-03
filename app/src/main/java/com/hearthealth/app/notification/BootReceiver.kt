package com.hearthealth.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.hearthealth.app.HeartHealthApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val db = (context.applicationContext as HeartHealthApp).database

        CoroutineScope(Dispatchers.IO).launch {
            val medications = db.medicationDao().getActiveMedications().first()
            for (med in medications) {
                val reminders = db.reminderDao().getRemindersForMedication(med.id).first()
                for (reminder in reminders) {
                    ReminderScheduler.scheduleReminder(context, med, reminder)
                }
            }
        }
    }
}
