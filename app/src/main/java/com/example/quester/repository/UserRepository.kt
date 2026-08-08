package com.example.quester.data.repository

import com.example.quester.data.dao.OwnedCosmeticDao
import com.example.quester.data.dao.UserDao
import com.example.quester.data.model.OwnedCosmetic
import com.example.quester.data.model.User
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val userDao: UserDao,
    private val ownedCosmeticDao: OwnedCosmeticDao? = null
) {
    companion object {
        private const val XP_PER_LEVEL = 100
    }

    suspend fun getUser(): User? = userDao.getUser()

    fun getUserFlow(): Flow<User?> = userDao.getUserFlow()

    suspend fun createInitialUser(username: String) {
        val existing = userDao.getUser()
        if (existing == null) {
            userDao.insertUser(
                User(
                    username = username,
                    passwordHash = "",
                    xpTotale = 0,
                    livello = 1,
                    coins = 0
                )
            )
        }
    }

    suspend fun addXp(xpGained: Int) {
        if (xpGained <= 0) return
        val current = userDao.getUser() ?: return
        val newXpTotal = current.xpTotale + xpGained
        val newLevel = (newXpTotal / XP_PER_LEVEL) + 1
        userDao.updateUser(current.copy(xpTotale = newXpTotal, livello = newLevel))
    }

    suspend fun updateUser(user: User) = userDao.updateUser(user)

    suspend fun updateUsername(newUsername: String) {
        val current = userDao.getUser() ?: return
        val clean = newUsername.trim()
        require(clean.isNotBlank()) { "Username vuoto" }
        userDao.updateUser(current.copy(username = clean))
    }

    suspend fun addCoins(amount: Int) {
        if (amount <= 0) return
        val current = userDao.getUser() ?: return
        userDao.updateUser(current.copy(coins = current.coins + amount))
    }

    suspend fun spendCoins(amount: Int): Boolean {
        if (amount <= 0) return false
        val current = userDao.getUser() ?: return false
        if (current.coins < amount) return false
        userDao.updateUser(current.copy(coins = current.coins - amount))
        return true
    }

    suspend fun unlockCosmetic(itemId: String) {
        val current = userDao.getUser() ?: return
        val ownedDao = ownedCosmeticDao ?: return
        ownedDao.insertOwned(OwnedCosmetic(userId = current.id, itemId = itemId))
    }

    suspend fun isCosmeticOwned(itemId: String): Boolean {
        val current = userDao.getUser() ?: return false
        val ownedDao = ownedCosmeticDao ?: return false
        return ownedDao.isOwned(current.id, itemId)
    }

    fun getOwnedCosmeticsFlow(userId: Long): Flow<List<OwnedCosmetic>> {
        val ownedDao = ownedCosmeticDao ?: throw IllegalStateException("OwnedCosmeticDao not provided")
        return ownedDao.getOwnedByUser(userId)
    }

    suspend fun getOwnedCosmetics(): List<OwnedCosmetic> {
        val current = userDao.getUser() ?: return emptyList()
        val ownedDao = ownedCosmeticDao ?: return emptyList()
        return ownedDao.getOwnedByUser(current.id).let { flow ->
            var list = emptyList<OwnedCosmetic>()
            flow.collect { list = it }
            list
        }
    }

    suspend fun deleteUserAndProgress() {
        userDao.deleteAllUsers()
    }
}