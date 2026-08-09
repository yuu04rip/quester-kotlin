package com.example.quester.data.repository

import com.example.quester.data.dao.OwnedCosmeticDao
import com.example.quester.data.dao.UserDao
import com.example.quester.data.model.OwnedCosmetic
import com.example.quester.data.model.User
import com.example.quester.ui.screens.calculateLevelFromXp
import com.example.quester.ui.screens.getXpInCurrentLevel
import com.example.quester.ui.screens.getXpRequiredForLevel  // ← AGGIUNGI QUESTO IMPORT
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class UserRepository(
    private val userDao: UserDao,
    private val ownedCosmeticDao: OwnedCosmeticDao? = null
) {
    companion object {
        // Costanti per messaggi di errore
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

    /**
     * Aggiunge XP all'utente e ricalcola il livello usando la formula esponenziale
     */
    suspend fun addXp(userId: Long, xpGained: Int) {
        if (xpGained <= 0) return

        val current = getUserById(userId) ?: userDao.getUser() ?: return

        // Calcola il nuovo XP totale
        val newXpTotal = current.xpTotale + xpGained

        // Calcola il nuovo livello usando la formula esponenziale
        val newLevel = calculateLevelFromXp(newXpTotal)

        userDao.updateUser(
            current.copy(
                xpTotale = newXpTotal,
                livello = newLevel
            )
        )
    }

    /**
     * Ottiene il livello attuale dell'utente calcolato dall'XP
     */
    suspend fun getCurrentLevel(userId: Long): Int {
        val user = getUserById(userId) ?: return 1
        return calculateLevelFromXp(user.xpTotale)
    }

    /**
     * Ottiene l'XP necessario per il prossimo livello
     */
    suspend fun getXpNeededForNextLevel(userId: Long): Int {
        val user = getUserById(userId) ?: return 100
        val currentLevel = calculateLevelFromXp(user.xpTotale)
        val xpInCurrent = getXpInCurrentLevel(user.xpTotale, currentLevel)
        val xpNeeded = getXpRequiredForLevel(currentLevel)
        return xpNeeded - xpInCurrent
    }

    /**
     * Ottiene il progresso verso il prossimo livello (0.0 - 1.0)
     */
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

    suspend fun updateUsername(userId: Long, newUsername: String) {
        val clean = newUsername.trim().lowercase()
        require(clean.isNotBlank()) { ERROR_USERNAME_EMPTY }

        val existing = userDao.getUserByUsername(clean)
        require(!(existing != null && existing.id != userId)) {
            ERROR_USERNAME_TAKEN
        }

        val current = getUserById(userId) ?: userDao.getUser() ?: return
        userDao.updateUser(current.copy(username = clean))
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