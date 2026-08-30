package com.example.quester.data.repository

import com.example.quester.data.dao.UserDao
import com.example.quester.data.model.User
import com.example.quester.domain.security.PasswordHasher

class AuthRepository(
    private val userDao: UserDao
) {
    suspend fun register(username: String, email: String?, password: String): AuthResult {
        val cleanUsername = username.trim().lowercase()
        val cleanEmail = email?.trim()?.lowercase()?.ifBlank { null }

        val existingUsername = userDao.getUserByUsername(cleanUsername)
        if (existingUsername != null) return AuthResult.Error("Username già esistente")

        if (cleanEmail != null) {
            val existingEmail = userDao.getUserByEmail(cleanEmail)
            if (existingEmail != null) return AuthResult.Error("Email già registrata")
        }

        val hash = PasswordHasher.hash(password)
        val user = User(username = cleanUsername, email = cleanEmail, passwordHash = hash)
        val id = userDao.insertUser(user)
        if (id == -1L) return AuthResult.Error("Errore creazione utente")

        val created = userDao.getUserByUsername(cleanUsername) ?: return AuthResult.Error("Errore creazione utente")
        return AuthResult.Success(created)
    }

    suspend fun login(identity: String, password: String): AuthResult {
        val cleanIdentity = identity.trim().lowercase()
        val user = userDao.getUserByIdentity(cleanIdentity) ?: return AuthResult.Error("Credenziali non valide")
        val ok = PasswordHasher.verify(password, user.passwordHash)
        return if (ok) AuthResult.Success(user) else AuthResult.Error("Credenziali non valide")
    }
}

sealed interface AuthResult {
    data class Success(val user: User) : AuthResult
    data class Error(val message: String) : AuthResult
}