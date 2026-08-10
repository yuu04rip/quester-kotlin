package com.example.quester.data.repository

import com.example.quester.data.dao.OwnedCosmeticDao
import com.example.quester.data.dao.UserDao
import com.example.quester.data.model.OwnedCosmetic
import com.example.quester.data.model.User
import com.example.quester.ui.screens.calculateLevelFromXp
import com.example.quester.ui.screens.getXpInCurrentLevel
import com.example.quester.ui.screens.getXpRequiredForLevel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class UserRepository(
    private val userDao: UserDao,
    private val ownedCosmeticDao: OwnedCosmeticDao? = null
) {
    companion object {
        private const val ERROR_USERNAME_EMPTY = "Username vuoto"
        private const val ERROR_USERNAME_TAKEN = "Username già in uso"
        private const val ERROR_OWNED_DAO_NOT_PROVIDED = "OwnedCosmeticDao non fornito"
    }

    // ===== METODI GET =====

    suspend fun getUserById(userId: Long): User? = userDao.getUserById(userId)
    suspend fun getUser(): User? = userDao.getUser()
    fun getUserFlow(): Flow<User?> = userDao.getUserFlow()
    fun getUserByIdFlow(userId: Long): Flow<User?> = userDao.getUserByIdFlow(userId)

    // ===== METODI XP =====

    suspend fun addXp(userId: Long, xpGained: Int) {
        if (xpGained <= 0) return
        val current = getUserById(userId) ?: userDao.getUser() ?: return
        val newXpTotal = current.xpTotale + xpGained
        val newLevel = calculateLevelFromXp(newXpTotal)
        userDao.updateUser(
            current.copy(
                xpTotale = newXpTotal,
                livello = newLevel
            )
        )
    }

    suspend fun getCurrentLevel(userId: Long): Int {
        val user = getUserById(userId) ?: return 1
        return calculateLevelFromXp(user.xpTotale)
    }

    suspend fun getXpNeededForNextLevel(userId: Long): Int {
        val user = getUserById(userId) ?: return 100
        val currentLevel = calculateLevelFromXp(user.xpTotale)
        val xpInCurrent = getXpInCurrentLevel(user.xpTotale, currentLevel)
        val xpNeeded = getXpRequiredForLevel(currentLevel)
        return xpNeeded - xpInCurrent
    }

    suspend fun getXpProgress(userId: Long): Float {
        val user = getUserById(userId) ?: return 0f
        val currentLevel = calculateLevelFromXp(user.xpTotale)
        val xpInCurrent = getXpInCurrentLevel(user.xpTotale, currentLevel)
        val xpNeeded = getXpRequiredForLevel(currentLevel)
        return (xpInCurrent.toFloat() / xpNeeded).coerceIn(0f, 1f)
    }

    // ===== METODI COINS =====

    suspend fun addCoins(userId: Long, amount: Int) {
        if (amount <= 0) return
        val current = getUserById(userId) ?: userDao.getUser() ?: return
        userDao.updateUser(current.copy(coins = current.coins + amount))
    }

    suspend fun spendCoins(userId: Long, amount: Int): Boolean {
        if (amount <= 0) return false
        val current = getUserById(userId) ?: userDao.getUser() ?: return false
        if (current.coins < amount) return false
        userDao.updateUser(current.copy(coins = current.coins - amount))
        return true
    }

    // ===== METODI UTENTE =====

    suspend fun updateUser(user: User) = userDao.updateUser(user)

    suspend fun updateUsername(userId: Long, newUsername: String): Boolean {
        val clean = newUsername.trim().lowercase()
        if (clean.isBlank() || clean.length < 3) return false

        val existing = userDao.getUserByUsername(clean)
        if (existing != null && existing.id != userId) return false

        val current = getUserById(userId) ?: return false
        userDao.updateUser(current.copy(username = clean))
        return true
    }

    suspend fun deleteAccount(userId: Long): Boolean {

        // Verifica direttamente se l'utente esiste
        if (getUserById(userId) == null) return false

        // Elimina cosmetici posseduti
        ownedCosmeticDao?.deleteAllForUser(userId)

        // Elimina utente (le missioni e subtask vengono eliminati via CASCADE)
        userDao.deleteUser(userId)
        return true
    }

    suspend fun updateProfileImage(userId: Long, uri: String?) {
        val current = getUserById(userId) ?: userDao.getUser() ?: return
        userDao.updateUser(current.copy(profileImageUri = uri))
    }

    suspend fun removeProfileImage(userId: Long) {
        updateProfileImage(userId, null)
    }

    // ===== METODI COSMETICI =====

    suspend fun unlockCosmetic(userId: Long, itemId: String) {
        val ownedDao = ownedCosmeticDao ?: return
        ownedDao.insertOwned(OwnedCosmetic(userId = userId, itemId = itemId))
    }

    suspend fun isCosmeticOwned(userId: Long, itemId: String): Boolean {
        val ownedDao = ownedCosmeticDao ?: return false
        return ownedDao.isOwned(userId, itemId)
    }

    suspend fun getOwnedCosmetics(userId: Long): List<OwnedCosmetic> {
        val ownedDao = ownedCosmeticDao ?: return emptyList()
        return ownedDao.getOwnedByUser(userId).first()
    }

    fun getOwnedCosmeticsFlow(userId: Long): Flow<List<OwnedCosmetic>> {
        val ownedDao = ownedCosmeticDao ?: throw IllegalStateException(ERROR_OWNED_DAO_NOT_PROVIDED)
        return ownedDao.getOwnedByUser(userId)
    }

    // ===== METODI UTILITY =====

    suspend fun deleteUserAndProgress() {
        userDao.deleteAllUsers()
    }
}