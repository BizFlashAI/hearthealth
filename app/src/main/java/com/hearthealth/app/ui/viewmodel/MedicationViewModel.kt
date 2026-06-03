package com.hearthealth.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hearthealth.app.HeartHealthApp
import com.hearthealth.app.data.entity.Medication
import com.hearthealth.app.data.entity.Reminder
import com.hearthealth.app.notification.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MedicationViewModel(application: Application) : AndroidViewModel(application) {

    private val db = (application as HeartHealthApp).database
    private val medicationDao = db.medicationDao()
    private val reminderDao = db.reminderDao()

    val medications: StateFlow<List<Medication>> = medicationDao.getAllMedications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedMedication = MutableStateFlow<Medication?>(null)
    val selectedMedication: StateFlow<Medication?> = _selectedMedication.asStateFlow()

    private val _remindersForSelected = MutableStateFlow<List<Reminder>>(emptyList())
    val remindersForSelected: StateFlow<List<Reminder>> = _remindersForSelected.asStateFlow()

    fun selectMedication(medication: Medication) {
        _selectedMedication.value = medication
        viewModelScope.launch {
            reminderDao.getRemindersForMedication(medication.id).collect {
                _remindersForSelected.value = it
            }
        }
    }

    fun addMedication(name: String, dosage: String) {
        viewModelScope.launch {
            medicationDao.insert(Medication(name = name, dosage = dosage))
        }
    }

    fun toggleMedicationStatus(medication: Medication) {
        viewModelScope.launch {
            val newActive = !medication.isActive
            medicationDao.updateActiveStatus(medication.id, newActive)

            val reminders = reminderDao.getRemindersForMedication(medication.id).first()
            val context = getApplication<HeartHealthApp>()
            if (newActive) {
                val updatedMed = medication.copy(isActive = true)
                reminders.forEach { ReminderScheduler.scheduleReminder(context, updatedMed, it) }
            } else {
                ReminderScheduler.cancelAllForMedication(context, reminders)
            }
        }
    }

    fun addReminder(medicationId: Long, hour: Int, minute: Int) {
        viewModelScope.launch {
            val reminderId = reminderDao.insert(
                Reminder(medicationId = medicationId, hour = hour, minute = minute)
            )
            val medication = medicationDao.getMedicationById(medicationId) ?: return@launch
            if (medication.isActive) {
                val reminder = reminderDao.getReminderById(reminderId) ?: return@launch
                ReminderScheduler.scheduleReminder(getApplication(), medication, reminder)
            }
        }
    }

    fun removeReminder(reminder: Reminder) {
        viewModelScope.launch {
            ReminderScheduler.cancelReminder(getApplication(), reminder.id)
            reminderDao.delete(reminder)
        }
    }

    fun deleteMedication(medication: Medication) {
        viewModelScope.launch {
            val reminders = reminderDao.getRemindersForMedication(medication.id).first()
            ReminderScheduler.cancelAllForMedication(getApplication(), reminders)
            medicationDao.delete(medication)
        }
    }
}
