package com.example.quester.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.quester.data.model.OwnedCosmetic
import com.example.quester.data.model.User
import com.example.quester.data.repository.UserRepository
import com.example.quester.data.session.SessionManager
import com.example.quester.ui.components.AvatarCosmetics
import com.example.quester.ui.components.CRTEffect
import com.example.quester.ui.components.FrameType
import com.example.quester.ui.components.HatType
import com.example.quester.ui.components.WeaponType
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

data class ProfileCallbacks(
    val onLogout: () -> Unit,
    val onDeleteAccount: () -> Unit,
    val onUpdateUsername: (String) -> Unit,
    val onShowCustomization: () -> Unit
)

@Composable
fun ProfileScreen(
    userRepository: UserRepository,
    sessionManager: SessionManager,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit,
    onUpdateUsername: (String) -> Unit,
    onShowCustomization: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    CRTEffect {
        val loggedUserId by sessionManager
            .loggedUserId
            .collectAsState(initial = null)

        val userFlow = remember(loggedUserId) {
            loggedUserId?.let { userRepository.getUserByIdFlow(it) } ?: flowOf(null)
        }
        val user by userFlow.collectAsState(initial = null)

        ProfileLoader(
            userId = loggedUserId,
            user = user,
            userRepository = userRepository,
            callbacks = ProfileCallbacks(
                onLogout = onLogout,
                onDeleteAccount = onDeleteAccount,
                onUpdateUsername = onUpdateUsername,
                onShowCustomization = onShowCustomization
            ),
            coroutineScope = coroutineScope
        )
    }
}

@Composable
private fun ProfileLoader(
    userId: Long?,
    user: User?,
    userRepository: UserRepository,
    callbacks: ProfileCallbacks,
    coroutineScope: kotlinx.coroutines.CoroutineScope
) {
    var ownedCosmetics by remember { mutableStateOf<List<OwnedCosmetic>>(emptyList()) }
    var showEditUsername by remember { mutableStateOf(false) }
    var showDeleteAccount by remember { mutableStateOf(false) }
    var usernameError by remember { mutableStateOf<String?>(null) }

    var xpProgress by remember { mutableFloatStateOf(0f) }
    var xpInCurrentLevel by remember { mutableIntStateOf(0) }
    var xpNeededForLevel by remember { mutableIntStateOf(100) }
    var levelUpCoins by remember { mutableIntStateOf(0) }

    // Ricarica le cosmetiche possedute sia al cambio dell'utente che alle modifiche utente
    LaunchedEffect(userId, user) {
        if (userId == null) return@LaunchedEffect

        ownedCosmetics = userRepository.getOwnedCosmetics(userId)

        user?.let { currentUser ->
            val level = currentUser.livello

            // USIAMO SEMPRE IL REPOSITORY: calcola correttamente i resti e i progressi basandosi sui 63700 XP
            xpInCurrentLevel = userRepository.getXpInCurrentLevel(currentUser.xpTotale, level)
            xpNeededForLevel = userRepository.getXpRequiredForLevel(level)
            xpProgress = userRepository.getXpProgress(currentUser.xpTotale, level)
            levelUpCoins = userRepository.getLevelUpCoins(level + 1)
        }
    }

    // Callback di salvataggio cosmetici con aggiornamento immediato dello stato locale
    val onSaveCosmetics: (AvatarCosmetics) -> Unit = { newCosmetics ->
        if (userId != null) {
            coroutineScope.launch {
                userRepository.updateUserCosmetics(
                    userId = userId,
                    hat = newCosmetics.hat.name,
                    weapon = newCosmetics.weapon.name,
                    frame = newCosmetics.frame.name
                )
                ownedCosmetics = userRepository.getOwnedCosmetics(userId)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val currentUser = user
        if (currentUser == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        } else {
            ProfileContent(
                user = currentUser,
                ownedCosmetics = ownedCosmetics,
                xpData = ProfileXpData(
                    xpProgress = xpProgress,
                    xpInCurrentLevel = xpInCurrentLevel,
                    xpNeededForLevel = xpNeededForLevel,
                    levelUpCoins = levelUpCoins
                ),
                uiState = ProfileUiState(
                    showEditUsername = showEditUsername,
                    showDeleteAccount = showDeleteAccount,
                    usernameError = usernameError
                ),
                actions = ProfileActions(
                    onLogout = callbacks.onLogout,
                    onDeleteAccount = callbacks.onDeleteAccount,
                    onUpdateUsername = callbacks.onUpdateUsername,
                    onShowEditUsername = {
                        usernameError = null
                        showEditUsername = true
                    },
                    onShowDeleteAccount = {
                        showDeleteAccount = true
                    },
                    onHideDialogs = {
                        showEditUsername = false
                        showDeleteAccount = false
                        usernameError = null
                    },
                    onShowCustomization = callbacks.onShowCustomization,
                    onSaveCosmetics = onSaveCosmetics
                )
            )
        }
    }
}

// ============================================================
// HELPER PARSERS CORRETTI
// ============================================================

fun parseHatType(value: String?): HatType {
    if (value.isNullOrBlank() || value.contains("NONE", ignoreCase = true)) return HatType.NONE
    return try { HatType.valueOf(value.uppercase()) } catch (_: Exception) { HatType.NONE }
}

fun parseWeaponType(value: String?): WeaponType {
    if (value.isNullOrBlank() || value.contains("NONE", ignoreCase = true)) return WeaponType.NONE
    return try { WeaponType.valueOf(value.uppercase()) } catch (_: Exception) { WeaponType.NONE }
}

fun parseFrameType(value: String?): FrameType {
    if (value.isNullOrBlank() || value.equals("NONE", ignoreCase = true)) {
        return FrameType.BASIC
    }

    return when (value.lowercase()) {
        "frame_basic", "basic" -> FrameType.BASIC
        "frame_mago", "mago" -> FrameType.MAGO
        "frame_cavaliere", "cavaliere" -> FrameType.CAVALIERE
        "frame_scifi", "sci_fi", "scifi" -> FrameType.SCI_FI
        else -> {
            try {
                FrameType.valueOf(value.uppercase())
            } catch (_: Exception) {
                FrameType.BASIC
            }
        }
    }
}