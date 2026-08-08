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

    suspend fun register(username: String, email: String?, password: String): AuthResult {
        val validationError = validateForRegister(username, email, password)
        if (validationError != null) return AuthResult.Error(validationError)

        val res = authRepository.register(username, email, password)
        if (res is AuthResult.Success) sessionManager.createSession(res.user.id)
        return res
    }

    suspend fun login(identity: String, password: String): AuthResult {
        if (identity.isBlank()) return AuthResult.Error("Username o email obbligatorio")
        if (password.isBlank()) return AuthResult.Error("Password obbligatoria")

        val res = authRepository.login(identity, password)
        if (res is AuthResult.Success) sessionManager.createSession(res.user.id)
        return res
    }

    suspend fun logout() = sessionManager.clearSession()

    private fun validateForRegister(username: String, email: String?, password: String): String? {
        val u = username.trim()
        val e = email?.trim()

        if (u.isBlank()) return "Username obbligatorio"
        if (u.length < 3) return "Username troppo corto (minimo 3 caratteri)"

        if (!e.isNullOrBlank()) {
            val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
            if (!emailRegex.matches(e)) return "Email non valida"
        }

        if (password.length < 8) return "Password troppo corta (minimo 8 caratteri)"
        if (!password.any { it.isDigit() }) return "La password deve contenere almeno 1 numero"
        if (!password.any { it.isUpperCase() }) return "La password deve contenere almeno 1 maiuscola"

        return null
    }
}