package com.example.quester.domain.service

import com.example.quester.data.repository.UserRepository
import com.example.quester.data.session.SessionManager
import kotlinx.coroutines.flow.first

class CurrencyService(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) {
    suspend fun onSpecialEvent(coins: Int) {
        val userId = sessionManager.loggedUserId.first() ?: return
        userRepository.addCoins(userId, coins.coerceAtLeast(0))
    }


}