package com.example.quester.domain.service

import com.example.quester.data.dao.OwnedCosmeticDao
import com.example.quester.data.dao.ShopDao
import com.example.quester.data.model.OwnedCosmetic
import com.example.quester.data.repository.UserRepository
import com.example.quester.data.session.SessionManager
import kotlinx.coroutines.flow.first

class ShopService(
    private val userRepository: UserRepository,
    private val shopDao: ShopDao,
    private val ownedDao: OwnedCosmeticDao,
    private val sessionManager: SessionManager
) {
    suspend fun purchase(itemId: String): PurchaseResult {
        val userId = sessionManager.loggedUserId.first() ?: return PurchaseResult.NoUser
        val user = userRepository.getUserById(userId) ?: return PurchaseResult.NoUser
        val item = shopDao.getItemById(itemId) ?: return PurchaseResult.ItemNotFound

        if (ownedDao.isOwned(user.id, item.id)) return PurchaseResult.AlreadyOwned
        if (user.coins < item.price) return PurchaseResult.InsufficientFunds

        val spent = userRepository.spendCoins(userId, item.price)
        if (!spent) return PurchaseResult.InsufficientFunds

        ownedDao.insertOwned(OwnedCosmetic(userId = user.id, itemId = item.id))
        return PurchaseResult.Success
    }
}

sealed interface PurchaseResult {
    data object Success : PurchaseResult
    data object NoUser : PurchaseResult
    data object ItemNotFound : PurchaseResult
    data object AlreadyOwned : PurchaseResult
    data object InsufficientFunds : PurchaseResult
}