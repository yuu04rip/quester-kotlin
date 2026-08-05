package com.example.quester.domain.service

import com.example.quester.data.model.User
import com.example.quester.data.repository.UserRepository

class AccountService(
    private val userRepository: UserRepository
) {
    suspend fun ensureUserExists(defaultUsername: String = "player"): User {
        val existing = userRepository.getUser()
        if (existing != null) return existing
        userRepository.createInitialUser(defaultUsername)
        return requireNotNull(userRepository.getUser())
    }

    suspend fun loginOrLoadUser(): User {
        return ensureUserExists()
    }

    suspend fun updateUsername(newUsername: String) {
        require(newUsername.isNotBlank()) { "Username vuoto" }
        userRepository.updateUsername(newUsername.trim())
    }

    suspend fun deleteAccountAndData() {
        // Se hai dao/repo multipli, fai cleanup orchestrato qui
        userRepository.deleteUserAndProgress()
    }
}