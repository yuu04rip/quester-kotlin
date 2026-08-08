package com.example.quester.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quester.data.repository.AuthResult
import com.example.quester.domain.service.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val loading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val error: String? = null
)

class AuthViewModel(
    private val authService: AuthService
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    init {
        // Ascolta lo stato reale della sessione DataStore all'avvio dell'app
        viewModelScope.launch {
            authService.isAuthenticated.collect { loggedIn ->
                _uiState.update { it.copy(isAuthenticated = loggedIn) }
            }
        }
    }

    fun register(username: String, email: String?, password: String) = viewModelScope.launch {
        _uiState.update { it.copy(loading = true, error = null) }
        when (val res = authService.register(username, email, password)) {
            is AuthResult.Success -> {
                _uiState.update { it.copy(loading = false, error = null) }
            }
            is AuthResult.Error -> {
                _uiState.update { it.copy(loading = false, error = res.message) }
            }
        }
    }

    fun login(identity: String, password: String) = viewModelScope.launch {
        _uiState.update { it.copy(loading = true, error = null) }
        when (val res = authService.login(identity, password)) {
            is AuthResult.Success -> {
                _uiState.update { it.copy(loading = false, error = null) }
            }
            is AuthResult.Error -> {
                _uiState.update { it.copy(loading = false, error = res.message) }
            }
        }
    }

    fun logout() = viewModelScope.launch {
        authService.logout()
    }
}