package com.example.quester.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quester.data.repository.AuthResult
import com.example.quester.domain.service.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    fun register(username: String, password: String) = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(loading = true, error = null)
        when (val res = authService.register(username, password)) {
            is AuthResult.Success -> _uiState.value = AuthUiState(isAuthenticated = true)
            is AuthResult.Error -> _uiState.value = AuthUiState(error = res.message)
        }
    }

    fun login(username: String, password: String) = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(loading = true, error = null)
        when (val res = authService.login(username, password)) {
            is AuthResult.Success -> _uiState.value = AuthUiState(isAuthenticated = true)
            is AuthResult.Error -> _uiState.value = AuthUiState(error = res.message)
        }
    }

    fun logout() = viewModelScope.launch {
        authService.logout()
        _uiState.value = AuthUiState(isAuthenticated = false)
    }
}