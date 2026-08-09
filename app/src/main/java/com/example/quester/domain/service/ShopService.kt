package com.example.quester.domain.service

import com.example.quester.data.dao.OwnedCosmeticDao
import com.example.quester.data.dao.ShopDao
import com.example.quester.data.model.OwnedCosmetic
import com.example.quester.data.model.ShopItem
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

        // 1. Verifica se l'oggetto esiste
        val item = shopDao.getItemByItemId(itemId) ?: return false

        // 2. Verifica se l'utente possiede già l'oggetto
        if (ownedDao.isOwned(userId, itemId)) {
            return false
        }

        // 3. Verifica se l'utente ha abbastanza monete
        val user = userRepository.getUserById(userId) ?: return false
        if (user.coins < item.price) {
            return false
        }

        // 4. Sottrai le monete
        val success = userRepository.spendCoins(userId, item.price)
        if (!success) return false

        // 5. Aggiungi l'oggetto ai posseduti
        userRepository.unlockCosmetic(userId, itemId)

        return true
    }

    // Metodo per ottenere tutti gli oggetti dello shop con stato "posseduto"
    suspend fun getShopItemsWithOwnership(): List<Pair<ShopItem, Boolean>> {
        val userId = sessionManager.loggedUserId.first()
        val allItems = shopDao.getAllItems()
        val ownedItems = if (userId != null) {
            userRepository.getOwnedCosmetics(userId).map { it.itemId }.toSet()
        } else {
            emptySet()
        }

        return allItems.map { item ->
            Pair(item, item.itemId in ownedItems)
        }
    }
}