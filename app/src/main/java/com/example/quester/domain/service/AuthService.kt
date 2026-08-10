package com.example.quester.domain.service

import com.example.quester.data.repository.AuthRepository
import com.example.quester.data.repository.AuthResult
import com.example.quester.data.repository.UserRepository
import com.example.quester.data.session.SessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class AuthService(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager,
    private val userRepository: UserRepository
) {
    val isAuthenticated: Flow<Boolean> = sessionManager.isLoggedIn

    suspend fun register(username: String, email: String?, password: String): AuthResult {
        val cleanUsername = username.trim().lowercase()
        val cleanEmail = email?.trim()?.lowercase()

        val validationError = validateForRegister(cleanUsername, cleanEmail, password)
        if (validationError != null) return AuthResult.Error(validationError)

        val res = authRepository.register(cleanUsername, cleanEmail, password)
        if (res is AuthResult.Success) {
            sessionManager.clearSession()
            sessionManager.createSession(res.user.id)
        }
        return res
    }

    suspend fun login(identity: String, password: String): AuthResult {
        val cleanIdentity = identity.trim().lowercase()

        if (cleanIdentity.isBlank()) return AuthResult.Error("Username o email obbligatorio")
        if (password.isBlank()) return AuthResult.Error("Password obbligatoria")

        val res = authRepository.login(cleanIdentity, password)
        if (res is AuthResult.Success) {
            sessionManager.clearSession()
            sessionManager.createSession(res.user.id)
        }
        return res
    }

    suspend fun logout() = sessionManager.clearSession()

    // ===== NUOVI METODI =====

    suspend fun updateUsername(newUsername: String): Boolean {
        val userId = sessionManager.loggedUserId.first() ?: return false
        return userRepository.updateUsername(userId, newUsername)
    }

    suspend fun deleteAccount(): Boolean {
        val userId = sessionManager.loggedUserId.first() ?: return false
        val result = userRepository.deleteAccount(userId)
        if (result) {
            sessionManager.clearSession()
        }
        return result
    }

    private fun validateForRegister(username: String, email: String?, password: String): String? {
        if (username.isBlank()) return "Username obbligatorio"
        if (username.length < 3) return "Username troppo corto (minimo 3 caratteri)"

        if (!email.isNullOrBlank()) {
            val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
            if (!emailRegex.matches(email)) return "Email non valida"
        }

        if (password.length < 8) return "Password troppo corta (minimo 8 caratteri)"
        if (!password.any { it.isDigit() }) return "La password deve contenere almeno 1 numero"
        if (!password.any { it.isUpperCase() }) return "La password deve contenere almeno 1 maiuscola"

        return null
    }
}