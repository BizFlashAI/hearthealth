package com.hearthealth.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hearthealth.app.HeartHealthApp
import com.hearthealth.app.data.entity.LifestyleData
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

class LifestyleViewModel(application: Application) : AndroidViewModel(application) {

    private val lifestyleDao = (application as HeartHealthApp).database.lifestyleDao()

    val latestData: StateFlow<LifestyleData?> = lifestyleDao.getLatest()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allData: StateFlow<List<LifestyleData>> = lifestyleDao.getAllData()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun injectMockData() {
        viewModelScope.launch {
            lifestyleDao.insert(
                LifestyleData(
                    exerciseMinutes = Random.nextInt(15, 90),
                    heartRate = Random.nextInt(60, 100),
                    stepCount = Random.nextInt(2000, 15000)
                )
            )
        }
    }
}
