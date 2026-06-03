package com.hearthealth.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hearthealth.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isSignedIn: Boolean = false,
    val isAnonymous: Boolean = false,
    val email: String? = null,
    val uid: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepo = AuthRepository()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepo.authStateFlow.collect { user ->
                _uiState.value = AuthUiState(
                    isSignedIn = user != null,
                    isAnonymous = user?.isAnonymous ?: false,
                    email = user?.email,
                    uid = user?.uid
                )
            }
        }
    }

    fun signInAnonymously() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            authRepo.signInAnonymously().fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = it.localizedMessage
                    )
                }
            )
        }
    }

    fun signInWithEmail(email: String, password: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            authRepo.signInWithEmail(email, password).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = it.localizedMessage
                    )
                }
            )
        }
    }

    fun createAccount(email: String, password: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            authRepo.createAccount(email, password).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = it.localizedMessage
                    )
                }
            )
        }
    }

    fun linkAnonymousToEmail(email: String, password: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            authRepo.linkAnonymousToEmail(email, password).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = it.localizedMessage
                    )
                }
            )
        }
    }

    fun signOut() {
        authRepo.signOut()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
