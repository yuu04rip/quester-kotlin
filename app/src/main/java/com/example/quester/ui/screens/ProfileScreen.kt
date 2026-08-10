package com.example.quester.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.example.quester.data.model.OwnedCosmetic
import com.example.quester.data.repository.UserRepository
import com.example.quester.data.session.SessionManager
import kotlinx.coroutines.flow.flowOf

@Composable
fun ProfileScreen(
    userRepository: UserRepository,
    sessionManager: SessionManager,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit,
    onUpdateUsername: (String) -> Unit,
    onShowCustomization: () -> Unit
) {
    val loggedUserId by sessionManager
        .loggedUserId
        .collectAsState(initial = null)

    val user by (
            loggedUserId?.let { userId ->
                userRepository.getUserByIdFlow(userId)
            } ?: flowOf(null)
            ).collectAsState(initial = null)

    var ownedCosmetics by remember {
        mutableStateOf<List<OwnedCosmetic>>(emptyList())
    }

    var showEditUsername by remember {
        mutableStateOf(false)
    }

    var showDeleteAccount by remember {
        mutableStateOf(false)
    }

    var usernameError by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(loggedUserId) {
        loggedUserId?.let { userId ->
            ownedCosmetics = userRepository.getOwnedCosmetics(userId)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surface.copy(
                            alpha = 0.8f
                        )
                    )
                )
            )
    ) {
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

                uiState = ProfileUiState(
                    showEditUsername = showEditUsername,
                    showDeleteAccount = showDeleteAccount,
                    usernameError = usernameError
                ),

                actions = ProfileActions(
                    onLogout = onLogout,

                    onDeleteAccount = onDeleteAccount,

                    onUpdateUsername = onUpdateUsername,

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

                    onShowCustomization = onShowCustomization
                )
            )
        }
    }
}

