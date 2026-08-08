package com.example.quester.domain.service

import com.example.quester.data.model.User
import com.example.quester.data.repository.UserRepository
import com.example.quester.data.session.SessionManager
import kotlinx.coroutines.flow.first

class AccountService(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) {
    /**
     * Recupera l'utente attualmente attivo dalla sessione.
     */
    suspend fun getCurrentUser(): User? {
        val userId = sessionManager.loggedUserId.first() ?: return null
        return userRepository.getUserById(userId)
    }

    /**
     * Aggiorna lo username dell'utente loggato.
     */
    suspend fun updateUsername(newUsername: String) {
        val userId = sessionManager.loggedUserId.first()
            ?: throw IllegalStateException("Nessun utente autenticato")

        require(newUsername.isNotBlank()) { "Username vuoto" }
        userRepository.updateUsername(userId, newUsername.trim())
    }

    /**
     * Elimina tutti i dati utente e svuota la sessione.
     */
    suspend fun deleteAccountAndData() {
        userRepository.deleteUserAndProgress()
        sessionManager.clearSession()
    }
}