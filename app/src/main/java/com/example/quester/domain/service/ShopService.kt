package com.example.quester.domain.service

import com.example.quester.data.dao.OwnedCosmeticDao
import com.example.quester.data.dao.ShopDao
import com.example.quester.data.repository.UserRepository
import com.example.quester.data.session.SessionManager
import kotlinx.coroutines.flow.first

class ShopService(
    private val userRepository: UserRepository,
    private val shopDao: ShopDao,
    private val ownedDao: OwnedCosmeticDao,
    private val sessionManager: SessionManager
) {

    suspend fun buyItem(itemId: String): Boolean {
        val userId = sessionManager.loggedUserId.first()
            ?: return false

        val item = shopDao.getItemByItemId(itemId) ?: return false

        if (ownedDao.isOwned(userId, itemId)) {
            return false
        }

        val user = userRepository.getUserById(userId) ?: return false
        if (user.coins < item.price) {
            return false
        }

        val success = userRepository.spendCoins(userId, item.price)
        if (!success) return false

        userRepository.unlockCosmetic(userId, itemId)

        return true
    }
}