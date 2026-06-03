package com.hearthealth.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hearthealth.app.HeartHealthApp
import com.hearthealth.app.data.entity.JournalEntry
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class JournalViewModel(application: Application) : AndroidViewModel(application) {

    private val journalDao = (application as HeartHealthApp).database.journalDao()

    val entries: StateFlow<List<JournalEntry>> = journalDao.getAllEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addEntry(feelings: String, symptoms: String, notes: String = "") {
        viewModelScope.launch {
            journalDao.insert(
                JournalEntry(feelings = feelings, symptoms = symptoms, notes = notes)
            )
        }
    }

    fun deleteEntry(entry: JournalEntry) {
        viewModelScope.launch {
            journalDao.delete(entry)
        }
    }
}
