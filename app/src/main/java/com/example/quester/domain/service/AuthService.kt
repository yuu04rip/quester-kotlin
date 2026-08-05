package com.example.quester.domain.service

import com.example.quester.data.repository.AuthRepository
import com.example.quester.data.repository.AuthResult
import com.example.quester.data.session.SessionManager
import kotlinx.coroutines.flow.Flow

class AuthService(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) {
    val isAuthenticated: Flow<Boolean> = sessionManager.isLoggedIn

    suspend fun register(username: String, password: String): AuthResult {
        val res = authRepository.register(username, password)
        if (res is AuthResult.Success) sessionManager.createSession(res.user.id)
        return res
    }

    suspend fun login(username: String, password: String): AuthResult {
        val res = authRepository.login(username, password)
        if (res is AuthResult.Success) sessionManager.createSession(res.user.id)
        return res
    }

    suspend fun logout() = sessionManager.clearSession()
}