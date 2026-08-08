package com.example.quester.domain.service

import com.example.quester.data.repository.UserRepository
import com.example.quester.data.session.SessionManager
import kotlinx.coroutines.flow.first

class GamificationService(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) {
    suspend fun awardCurrencyForLevelUp(levelsGained: Int) {
        if (levelsGained <= 0) return
        val userId = sessionManager.loggedUserId.first() ?: return
        val bonus = levelsGained * 50
        userRepository.addCoins(userId, bonus)
    }

    suspend fun awardCurrencyForSpecialEvent(amount: Int) {
        require(amount >= 0)
        val userId = sessionManager.loggedUserId.first() ?: return
        userRepository.addCoins(userId, amount)
    }
}