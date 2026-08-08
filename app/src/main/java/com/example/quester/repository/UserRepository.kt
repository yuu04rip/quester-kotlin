package com.example.quester.data.repository

import com.example.quester.data.dao.OwnedCosmeticDao
import com.example.quester.data.dao.UserDao
import com.example.quester.data.model.OwnedCosmetic
import com.example.quester.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.Calendar

class UserRepository(
    private val userDao: UserDao,
    private val ownedCosmeticDao: OwnedCosmeticDao? = null
) {
    companion object {
        private const val XP_PER_LEVEL = 100
        private const val MAX_DAILY_XP = 500
        private const val REPUTATION_PENALTY = 10

        // Costanti per messaggi di errore
        private const val ERROR_USERNAME_EMPTY = "Username vuoto"
        private const val ERROR_USERNAME_TAKEN = "Username già in uso"
        private const val ERROR_MAX_DAILY_XP = "Hai raggiunto il limite di $MAX_DAILY_XP XP giornalieri"
        private const val ERROR_OWNED_DAO_NOT_PROVIDED = "OwnedCosmeticDao non fornito"
    }

    suspend fun getUserById(userId: Long): User? = userDao.getUserById(userId)

    suspend fun getUser(): User? = userDao.getUser()

    fun getUserFlow(): Flow<User?> = userDao.getUserFlow()

    fun getUserByIdFlow(userId: Long): Flow<User?> = userDao.getUserByIdFlow(userId)

    suspend fun addXp(userId: Long, xpGained: Int) {
        if (xpGained <= 0) return

        val current = getUserById(userId) ?: userDao.getUser() ?: return

        // Resetta XP giornalieri se necessario
        val userWithReset = resetDailyXpIfNeeded(current)

        // Verifica limite XP giornaliero
        check(userWithReset.xpEarnedToday + xpGained <= MAX_DAILY_XP) {
            ERROR_MAX_DAILY_XP
        }

        val newXpTotal = userWithReset.xpTotale + xpGained
        val newLevel = (newXpTotal / XP_PER_LEVEL) + 1

        userDao.updateUser(
            userWithReset.copy(
                xpTotale = newXpTotal,
                livello = newLevel,
                xpEarnedToday = userWithReset.xpEarnedToday + xpGained
            )
        )
    }

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

    suspend fun unlockCosmetic(userId: Long, itemId: String) {
        val ownedDao = ownedCosmeticDao ?: return
        ownedDao.insertOwned(OwnedCosmetic(userId = userId, itemId = itemId))
    }

    suspend fun isCosmeticOwned(userId: Long, itemId: String): Boolean {
        val ownedDao = ownedCosmeticDao ?: return false
        return ownedDao.isOwned(userId, itemId)
    }

    fun getOwnedCosmeticsFlow(userId: Long): Flow<List<OwnedCosmetic>> {
        val ownedDao = ownedCosmeticDao ?: throw IllegalStateException(ERROR_OWNED_DAO_NOT_PROVIDED)
        return ownedDao.getOwnedByUser(userId)
    }

    suspend fun getOwnedCosmetics(userId: Long): List<OwnedCosmetic> {
        val ownedDao = ownedCosmeticDao ?: return emptyList()
        return ownedDao.getOwnedByUser(userId).first()
    }

    suspend fun deleteUserAndProgress() {
        userDao.deleteAllUsers()
    }

    // --- METODI PER SICUREZZA ---

    private suspend fun resetDailyXpIfNeeded(user: User): User {
        val today = getStartOfDay()
        if (user.lastXpResetDate < today) {
            userDao.resetDailyXp(user.id, today)
            return user.copy(xpEarnedToday = 0, lastXpResetDate = today)
        }
        return user
    }

    suspend fun getXpEarnedToday(userId: Long): Int {
        val user = getUserById(userId) ?: return 0
        val resetUser = resetDailyXpIfNeeded(user)
        return resetUser.xpEarnedToday
    }

    suspend fun decreaseReputation(userId: Long, amount: Int = REPUTATION_PENALTY) {
        userDao.updateReputation(userId, -amount)
    }

    suspend fun increaseReputation(userId: Long, amount: Int) {
        userDao.updateReputation(userId, amount)
    }

    suspend fun addWarning(userId: Long) {
        userDao.incrementWarnings(userId)
    }

    private fun getStartOfDay(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}