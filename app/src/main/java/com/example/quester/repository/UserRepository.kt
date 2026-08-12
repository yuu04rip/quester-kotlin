package com.example.quester.data.repository

import com.example.quester.data.dao.OwnedCosmeticDao
import com.example.quester.data.dao.UserDao
import com.example.quester.data.model.OwnedCosmetic
import com.example.quester.data.model.User
import com.example.quester.ui.components.AvatarCosmetics
import com.example.quester.ui.components.FrameType
import com.example.quester.ui.components.HatType
import com.example.quester.ui.components.WeaponType
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
        private const val MAX_LEVEL = 50
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
        return XP_BASE + (level - 1) * XP_INCREMENT
    }

    fun calculateLevelFromXp(totalXp: Int): Int {
        var remainingXp = totalXp
        var level = 1

        while (level < MAX_LEVEL) {
            val xpNeeded = getXpRequiredForLevel(level)
            if (remainingXp >= xpNeeded) {
                remainingXp -= xpNeeded
                level++
            } else {
                break
            }
        }

        return level.coerceAtMost(MAX_LEVEL)
    }

    fun getXpInCurrentLevel(
        totalXp: Int,
        level: Int = calculateLevelFromXp(totalXp)
    ): Int {
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
        val oldLevel = calculateLevelFromXp(current.xpTotale)
        val newXpTotal = current.xpTotale + xpGained
        val newLevel = calculateLevelFromXp(newXpTotal)

        var updatedUser = current.copy(
            xpTotale = newXpTotal,
            livello = newLevel
        )

        if (newLevel > oldLevel) {
            val coinsEarned = getLevelUpCoins(newLevel)
            updatedUser = updatedUser.copy(
                coins = updatedUser.coins + coinsEarned
            )
        }

        userDao.updateUser(updatedUser)
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

    suspend fun updateProfileImage(
        userId: Long,
        uri: String?
    ) {
        val current = getUserById(userId) ?: return
        userDao.updateUser(current.copy(profileImageUri = uri))
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
            hat = parseHat(user.equippedHat),
            weapon = parseWeapon(user.equippedWeapon),
            frame = parseFrame(user.equippedFrame)
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
                equippedHat = parseHat(hat).name,
                equippedWeapon = parseWeapon(weapon).name,
                equippedFrame = parseFrame(frame).name
            )
        )
    }

    // Parser universali per prevenire mismatch tra "NONE", "HAT_NONE", "", null
    private fun parseHat(value: String?): HatType {
        if (value.isNullOrBlank() || value.contains("NONE", ignoreCase = true)) return HatType.NONE
        return try { HatType.valueOf(value) } catch (_: Exception) { HatType.NONE }
    }

    private fun parseWeapon(value: String?): WeaponType {
        if (value.isNullOrBlank() || value.contains("NONE", ignoreCase = true)) return WeaponType.NONE
        return try { WeaponType.valueOf(value) } catch (_: Exception) { WeaponType.NONE }
    }

    private fun parseFrame(value: String?): FrameType {
        if (value.isNullOrBlank() || value.contains("NONE", ignoreCase = true)) return FrameType.NONE
        return try { FrameType.valueOf(value) } catch (_: Exception) { FrameType.NONE }
    }

    // ============================================================
    // UTILITY
    // ============================================================

    suspend fun deleteUserAndProgress() {
        userDao.deleteAllUsers()
    }
}