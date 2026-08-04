package com.example.quester.data.repository

import com.example.quester.data.dao.UserDao
import com.example.quester.data.model.User
import com.example.quester.domain.security.PasswordHasher

class AuthRepository(
    private val userDao: UserDao
) {
    suspend fun register(username: String, password: String): AuthResult {
        val u = username.trim()
        if (u.isBlank()) return AuthResult.Error("Username obbligatorio")
        if (password.length < 6) return AuthResult.Error("Password troppo corta (min 6)")

        val existing = userDao.getUserByUsername(u)
        if (existing != null) return AuthResult.Error("Username già esistente")

        val hash = PasswordHasher.hash(password)
        val user = User(username = u, passwordHash = hash)
        val id = userDao.insertUser(user)
        if (id == -1L) return AuthResult.Error("Errore creazione utente")

        val created = userDao.getUserByUsername(u) ?: return AuthResult.Error("Errore creazione utente")
        return AuthResult.Success(created)
    }

    suspend fun login(username: String, password: String): AuthResult {
        val u = username.trim()
        val user = userDao.getUserByUsername(u) ?: return AuthResult.Error("Credenziali non valide")
        val ok = PasswordHasher.verify(password, user.passwordHash)
        return if (ok) AuthResult.Success(user) else AuthResult.Error("Credenziali non valide")
    }
}

sealed interface AuthResult {
    data class Success(val user: User) : AuthResult
    data class Error(val message: String) : AuthResult
}