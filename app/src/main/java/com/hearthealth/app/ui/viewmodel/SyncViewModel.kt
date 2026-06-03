package com.hearthealth.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hearthealth.app.HeartHealthApp
import com.hearthealth.app.data.sync.FirestoreSyncService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class SyncUiState(
    val isSyncing: Boolean = false,
    val lastSyncResult: String? = null,
    val error: String? = null
)

class SyncViewModel(application: Application) : AndroidViewModel(application) {

    private val db = (application as HeartHealthApp).database
    private val syncService = FirestoreSyncService()

    private val _uiState = MutableStateFlow(SyncUiState())
    val uiState: StateFlow<SyncUiState> = _uiState.asStateFlow()

    fun pushToCloud() {
        _uiState.value = SyncUiState(isSyncing = true)
        viewModelScope.launch {
            try {
                val medications = db.medicationDao().getAllMedications().first()
                val reminders = db.reminderDao().getAllReminders().first()
                val journals = db.journalDao().getAllEntries().first()
                val lifestyle = db.lifestyleDao().getAllData().first()

                syncService.pushAllData(medications, reminders, journals, lifestyle)

                _uiState.value = SyncUiState(
                    lastSyncResult = "Pushed ${medications.size} meds, ${reminders.size} reminders, " +
                            "${journals.size} journal entries, ${lifestyle.size} lifestyle records"
                )
            } catch (e: Exception) {
                _uiState.value = SyncUiState(error = "Push failed: ${e.localizedMessage}")
            }
        }
    }

    fun pullFromCloud() {
        _uiState.value = SyncUiState(isSyncing = true)
        viewModelScope.launch {
            try {
                val medications = syncService.fetchMedications()
                val reminders = syncService.fetchReminders()
                val journals = syncService.fetchJournalEntries()
                val lifestyle = syncService.fetchLifestyleData()

                medications.forEach { db.medicationDao().insert(it) }
                reminders.forEach { db.reminderDao().insert(it) }
                journals.forEach { db.journalDao().insert(it) }
                lifestyle.forEach { db.lifestyleDao().insert(it) }

                _uiState.value = SyncUiState(
                    lastSyncResult = "Pulled ${medications.size} meds, ${reminders.size} reminders, " +
                            "${journals.size} journal entries, ${lifestyle.size} lifestyle records"
                )
            } catch (e: Exception) {
                _uiState.value = SyncUiState(error = "Pull failed: ${e.localizedMessage}")
            }
        }
    }

    fun clearStatus() {
        _uiState.value = SyncUiState()
    }
}
