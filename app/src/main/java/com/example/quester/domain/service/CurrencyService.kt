package com.example.quester.domain.service

import com.example.quester.data.repository.UserRepository
import com.example.quester.data.session.SessionManager
import kotlinx.coroutines.flow.first

class CurrencyService(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) {
    fun coinsForLevel(levelsGained: Int): Int = (levelsGained.coerceAtLeast(0)) * 50

    suspend fun onLevelUp(beforeLevel: Int, afterLevel: Int) {
        val userId = sessionManager.loggedUserId.first() ?: return
        val gained = (afterLevel - beforeLevel).coerceAtLeast(0)
        val coins = coinsForLevel(gained)
        userRepository.addCoins(userId, coins)
    }

    suspend fun onSpecialEvent(coins: Int) {
        val userId = sessionManager.loggedUserId.first() ?: return
        userRepository.addCoins(userId, coins.coerceAtLeast(0))
    }

    suspend fun onMissionRedeemed() {
        val userId = sessionManager.loggedUserId.first() ?: return
        userRepository.addCoins(userId, 20) // reward fisso MVP
    }
}