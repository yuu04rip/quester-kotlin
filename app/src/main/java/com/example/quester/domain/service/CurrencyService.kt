package com.example.quester.domain.service

import com.example.quester.data.repository.UserRepository

class CurrencyService(
    private val userRepository: UserRepository
) {
    fun coinsForLevel(levelsGained: Int): Int = (levelsGained.coerceAtLeast(0)) * 50

    suspend fun onLevelUp(beforeLevel: Int, afterLevel: Int) {
        val gained = (afterLevel - beforeLevel).coerceAtLeast(0)
        val coins = coinsForLevel(gained)
        userRepository.addCoins(coins)
    }

    suspend fun onSpecialEvent(coins: Int) {
        userRepository.addCoins(coins.coerceAtLeast(0))
    }

    suspend fun onMissionRedeemed() {
        userRepository.addCoins(20) // reward fisso MVP
    }
}