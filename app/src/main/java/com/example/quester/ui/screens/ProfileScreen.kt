package com.example.quester.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.quester.data.model.OwnedCosmetic
import com.example.quester.data.repository.UserRepository
import com.example.quester.data.session.SessionManager
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    userRepository: UserRepository,
    sessionManager: SessionManager,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val loggedUserId by sessionManager.loggedUserId.collectAsState(
        initial = null
    )

    val user by (
            loggedUserId?.let { userId ->
                userRepository.getUserByIdFlow(userId)
            } ?: flowOf(null)
            ).collectAsState(initial = null)

    var ownedCosmetics by remember {
        mutableStateOf<List<OwnedCosmetic>>(emptyList())
    }

    LaunchedEffect(loggedUserId) {
        loggedUserId?.let { userId ->
            ownedCosmetics = userRepository.getOwnedCosmetics(userId)
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->

        if (uri != null && loggedUserId != null) {

            runCatching {
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    flag
                )
            }

            scope.launch {
                userRepository.updateProfileImage(
                    loggedUserId!!,
                    uri.toString()
                )
            }
        }
    }

    val onPickImage: () -> Unit = {
        photoPickerLauncher.launch(
            PickVisualMediaRequest(
                ActivityResultContracts.PickVisualMedia.ImageOnly
            )
        )
    }

    val onRemoveImage: () -> Unit = {
        loggedUserId?.let { userId ->
            scope.launch {
                userRepository.removeProfileImage(userId)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF120C1E),
                        FantasyBackground,
                        Color(0xFF0B0813)
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
                    color = FantasyGold
                )
            }

        } else {

            ProfileContent(
                user = currentUser,
                ownedCosmetics = ownedCosmetics,
                onPickImage = onPickImage,
                onRemoveImage = onRemoveImage,
                onLogout = onLogout
            )
        }
    }
}