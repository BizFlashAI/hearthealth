package com.hearthealth.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.hearthealth.app.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GeminiUiState(
    val isLoading: Boolean = false,
    val result: String = "",
    val error: String? = null
)

class GeminiViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(GeminiUiState())
    val uiState: StateFlow<GeminiUiState> = _uiState.asStateFlow()

    private val generativeModel: GenerativeModel? = if (BuildConfig.GEMINI_API_KEY.isNotBlank()) {
        GenerativeModel(
            modelName = "gemini-2.0-flash",
            apiKey = BuildConfig.GEMINI_API_KEY
        )
    } else {
        null
    }

    fun fetchMedicationInfo(medicationName: String) {
        if (generativeModel == null) {
            _uiState.value = GeminiUiState(
                error = "Gemini API key not configured. Add GEMINI_API_KEY to local.properties."
            )
            return
        }

        _uiState.value = GeminiUiState(isLoading = true)

        viewModelScope.launch {
            try {
                val prompt = buildPrompt(medicationName)
                val response = generativeModel.generateContent(prompt)
                _uiState.value = GeminiUiState(
                    result = response.text ?: "No information returned."
                )
            } catch (e: Exception) {
                _uiState.value = GeminiUiState(
                    error = "Failed to fetch info: ${e.localizedMessage}"
                )
            }
        }
    }

    fun clearState() {
        _uiState.value = GeminiUiState()
    }

    private fun buildPrompt(medicationName: String): String {
        return """
            You are a helpful medical information assistant. The user wants to understand 
            their medication better. Provide a structured, user-friendly breakdown for the 
            medication "$medicationName".
            
            Please include the following sections:
            
            **What It Is**: A brief, plain-language description of the medication.
            
            **What It's Used For**: Common conditions or symptoms it treats.
            
            **How It Works**: A simple explanation of the mechanism of action.
            
            **Common Dosage Info**: Typical dosage ranges (note: always defer to their doctor).
            
            **Common Side Effects**: List the most frequently reported side effects.
            
            **Important Warnings**: Key interactions or precautions to be aware of.
            
            **Tips**: Any practical tips for taking this medication.
            
            IMPORTANT: Always include a disclaimer that this information is for educational 
            purposes only and the user should consult their healthcare provider for medical advice.
            
            Format the response in clean markdown with headers.
        """.trimIndent()
    }
}
