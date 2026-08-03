package com.example.quester.data.repository

import com.example.quester.data.dao.UserDao
import com.example.quester.data.model.User

/**
 * Repository utente:
 * - gestisce profilo
 * - gestisce XP e level-up
 */
class UserRepository(
    private val userDao: UserDao
) {
    companion object {
        // Soglia semplice MVP: ogni 100 XP sali di 1 livello
        private const val XP_PER_LEVEL = 100
    }

    suspend fun getUser(): User? = userDao.getUser()

    suspend fun createInitialUser(username: String) {
        val existing = userDao.getUser()
        if (existing == null) {
            userDao.insertUser(User(username = username))
        }
    }

    suspend fun addXp(xpGained: Int) {
        val current = userDao.getUser() ?: return
        val newXpTotal = current.xpTotale + xpGained
        val newLevel = (newXpTotal / XP_PER_LEVEL) + 1

        userDao.updateUser(
            current.copy(
                xpTotale = newXpTotal,
                livello = newLevel
            )
        )
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }
}