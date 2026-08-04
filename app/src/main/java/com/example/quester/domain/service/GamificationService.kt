package com.example.quester.domain.service

import com.example.quester.data.repository.UserRepository

class GamificationService(
    private val userRepository: UserRepository
) {
    suspend fun awardCurrencyForLevelUp(levelsGained: Int) {
        if (levelsGained <= 0) return
        val bonus = levelsGained * 50
        userRepository.addCoins(bonus)
    }

    suspend fun awardCurrencyForSpecialEvent(amount: Int) {
        require(amount >= 0)
        userRepository.addCoins(amount)
    }
}