package com.example.quester.data.repository

import com.example.quester.data.dao.OwnedCosmeticDao
import com.example.quester.data.dao.UserDao
import com.example.quester.data.model.OwnedCosmetic
import com.example.quester.data.model.User
import com.example.quester.ui.components.AvatarCosmetics
import com.example.quester.utils.CosmeticIdMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class UserRepository(
    private val userDao: UserDao,
    private val ownedCosmeticDao: OwnedCosmeticDao? = null
) {

    companion object {
        private const val ERROR_OWNED_DAO_NOT_PROVIDED = "OwnedCosmeticDao non fornito"

        private const val XP_BASE = 100
        private const val XP_INCREMENT = 50

        private const val MAX_LEVEL = User.MAX_LEVEL
        private const val MAX_TOTAL_XP = User.MAX_TOTAL_XP
    }

    // ============================================================
    // UTENTE
    // ============================================================

    suspend fun getUserById(userId: Long): User? =
        userDao.getUserById(userId)

    fun getUserByIdFlow(userId: Long): Flow<User?> =
        userDao.getUserByIdFlow(userId)

    // ============================================================
    // XP
    // ============================================================

    fun getXpRequiredForLevel(level: Int): Int {
        val effectiveLevel = level.coerceAtMost(MAX_LEVEL - 1)
        // Se siamo al livello 49, servono 2550 XP per raggiungere il 50 (soglia 63.750)
        return if (effectiveLevel == 49) 2550 else XP_BASE + (effectiveLevel - 1) * XP_INCREMENT
    }

    fun calculateLevelFromXp(totalXp: Int): Int {
        return User.calculateLevel(totalXp)
    }

    fun getXpInCurrentLevel(
        totalXp: Int,
        level: Int = calculateLevelFromXp(totalXp)
    ): Int {
        if (totalXp >= MAX_TOTAL_XP || level >= MAX_LEVEL) {
            return getXpRequiredForLevel(MAX_LEVEL - 1)
        }

        var xpForPreviousLevels = 0
        for (i in 1 until level) {
            xpForPreviousLevels += getXpRequiredForLevel(i)
        }
        return (totalXp - xpForPreviousLevels).coerceAtLeast(0)
    }

    fun getXpProgress(
        totalXp: Int,
        level: Int = calculateLevelFromXp(totalXp)
    ): Float {
        if (level >= MAX_LEVEL) return 1f

        val xpInCurrent = getXpInCurrentLevel(totalXp, level)
        val xpNeeded = getXpRequiredForLevel(level)
        return (xpInCurrent.toFloat() / xpNeeded).coerceIn(0f, 1f)
    }

    fun getLevelUpCoins(level: Int): Int {
        return when (level) {
            in 1..10 -> 3
            in 11..20 -> 5
            in 21..30 -> 8
            in 31..40 -> 12
            in 41..50 -> 20
            else -> 0
        }
    }

    suspend fun addXp(
        userId: Long,
        xpGained: Int
    ) {
        if (xpGained <= 0) return

        val current = getUserById(userId) ?: return

        // Se l'utente ha già raggiunto il massimo, blocchiamo l'incremento degli XP totali
        if (current.xpTotale >= MAX_TOTAL_XP) return

        val oldLevel = current.livello

        // Aggiungiamo gli XP bloccando rigorosamente il totale al tetto massimo di 63.750
        val newXpTotal = (current.xpTotale + xpGained).coerceAtMost(MAX_TOTAL_XP)
        val newLevel = calculateLevelFromXp(newXpTotal)

        var updatedUser = current.copy(
            xpTotale = newXpTotal,
            livello = newLevel // Aggiorna fisicamente anche il livello salvato nel DB
        )

        if (newLevel > oldLevel) {
            val coinsEarned = getLevelUpCoins(newLevel)
            updatedUser = updatedUser.copy(
                coins = updatedUser.coins + coinsEarned
            )
        }

        userDao.updateUser(updatedUser)

        // 👑 SBLOCCO AUTOMATICO TEMA REGALE E CORONA AL RAGGIUNGIMENTO DEL LIVELLO 50
        if (newLevel >= 50 && ownedCosmeticDao != null) {
            val rewards = listOf("reward_tema_regale", "reward_corona")
            rewards.forEach { itemId ->
                val alreadyOwned = ownedCosmeticDao.isOwned(userId, itemId)
                if (!alreadyOwned) {
                    ownedCosmeticDao.insertOwned(
                        OwnedCosmetic(
                            userId = userId,
                            itemId = itemId
                        )
                    )
                }
            }
        }
    }

    // ============================================================
    // COINS
    // ============================================================

    suspend fun addCoins(
        userId: Long,
        amount: Int
    ) {
        if (amount <= 0) return
        val current = getUserById(userId) ?: return
        userDao.updateUser(current.copy(coins = current.coins + amount))
    }

    suspend fun spendCoins(
        userId: Long,
        amount: Int
    ): Boolean {
        if (amount <= 0) return false
        val current = getUserById(userId) ?: return false
        if (current.coins < amount) return false
        userDao.updateUser(current.copy(coins = current.coins - amount))
        return true
    }

    // ============================================================
    // MODIFICA UTENTE
    // ============================================================

    suspend fun updateUsername(
        userId: Long,
        newUsername: String
    ): Boolean {
        val clean = newUsername.trim().lowercase()
        if (clean.isBlank() || clean.length < 3) return false

        val existing = userDao.getUserByUsername(clean)
        if (existing != null && existing.id != userId) return false

        val current = getUserById(userId) ?: return false
        userDao.updateUser(current.copy(username = clean))
        return true
    }

    suspend fun deleteAccount(
        userId: Long
    ): Boolean {
        if (getUserById(userId) == null) return false
        ownedCosmeticDao?.deleteAllForUser(userId)
        userDao.deleteUser(userId)
        return true
    }


    // ============================================================
    // COSMETICI SHOP
    // ============================================================

    suspend fun unlockCosmetic(
        userId: Long,
        itemId: String
    ) {
        val ownedDao = ownedCosmeticDao ?: return
        ownedDao.insertOwned(OwnedCosmetic(userId = userId, itemId = itemId))
    }

    suspend fun getOwnedCosmetics(
        userId: Long
    ): List<OwnedCosmetic> {
        val ownedDao = ownedCosmeticDao ?: return emptyList()
        return ownedDao.getOwnedByUser(userId).first()
    }

    fun getOwnedCosmeticsFlow(
        userId: Long
    ): Flow<List<OwnedCosmetic>> {
        val ownedDao = ownedCosmeticDao
            ?: throw IllegalStateException(ERROR_OWNED_DAO_NOT_PROVIDED)
        return ownedDao.getOwnedByUser(userId)
    }

    // ============================================================
    // COSMETICI EQUIPAGGIATI
    // ============================================================

    suspend fun getEquippedCosmetics(userId: Long): AvatarCosmetics {
        val user = getUserById(userId) ?: return AvatarCosmetics()

        return AvatarCosmetics(
            hat = CosmeticIdMapper.parseHatType(user.equippedHat),
            weapon = CosmeticIdMapper.parseWeaponType(user.equippedWeapon),
            frame = CosmeticIdMapper.parseFrameType(user.equippedFrame)
        )
    }

    suspend fun saveEquippedCosmetics(
        userId: Long,
        cosmetics: AvatarCosmetics
    ) {
        val current = getUserById(userId) ?: return

        userDao.updateUser(
            current.copy(
                equippedHat = cosmetics.hat.name,
                equippedWeapon = cosmetics.weapon.name,
                equippedFrame = cosmetics.frame.name
            )
        )
    }

    suspend fun updateUserCosmetics(
        userId: Long,
        hat: String?,
        weapon: String?,
        frame: String?
    ) {
        val current = getUserById(userId) ?: return

        userDao.updateUser(
            current.copy(
                equippedHat = CosmeticIdMapper.parseHatType(hat).name,
                equippedWeapon = CosmeticIdMapper.parseWeaponType(weapon).name,
                equippedFrame = CosmeticIdMapper.parseFrameType(frame).name
            )
        )
    }

    // ============================================================
    // UTILITY
    // ============================================================

    suspend fun deleteUserAndProgress() {
        userDao.deleteAllUsers()
    }
}