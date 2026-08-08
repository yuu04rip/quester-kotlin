package com.example.quester.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.quester.R
import com.example.quester.data.model.OwnedCosmetic
import com.example.quester.data.model.User
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

    val loggedUserId by sessionManager.loggedUserId.collectAsState(initial = null)
    val user by (loggedUserId?.let { userRepository.getUserByIdFlow(it) } ?: flowOf(null))
        .collectAsState(initial = null)
    val ownedCosmetics by (loggedUserId?.let { userRepository.getOwnedCosmeticsFlow(it) } ?: flowOf(emptyList()))
        .collectAsState(initial = emptyList())

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null && loggedUserId != null) {
            runCatching {
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri, flag)
            }
            scope.launch {
                userRepository.updateProfileImage(loggedUserId!!, uri.toString())
            }
        }
    }

    val onPickImage = {
        photoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    val onRemoveImage = {
        loggedUserId?.let { userId ->
            scope.launch {
                userRepository.removeProfileImage(userId)
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val currentUser = user
        if (currentUser == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            ProfileContent(
                user = currentUser,
                ownedCosmetics = ownedCosmetics,
                onPickImage = onPickImage,
                onRemoveImage = { onRemoveImage() },
                onLogout = onLogout
            )
        }
    }
}

@Composable
private fun ProfileContent(
    user: User,
    ownedCosmetics: List<OwnedCosmetic>,
    onPickImage: () -> Unit,
    onRemoveImage: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        ProfileAvatar(
            profileImageUri = user.profileImageUri,
            onPickImage = onPickImage,
            onRemoveImage = onRemoveImage
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = user.username,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Livello ${user.livello}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontFamily = FontFamily.SansSerif
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                icon = Icons.Default.Star,
                value = "${user.xpTotale}",
                label = "XP Totali",
                color = Color(0xFFFFB300)
            )
            CoinStatItem(
                value = "${user.coins}",
                label = "Monete"
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "I Tuoi Cosmetici",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.Start),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        CosmeticsGrid(
            ownedCosmetics = ownedCosmetics,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Esci dall'Account")
        }
    }
}

@Composable
private fun ProfileAvatar(
    profileImageUri: String?,
    onPickImage: () -> Unit,
    onRemoveImage: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.size(108.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable { showMenu = true },
            contentAlignment = Alignment.Center
        ) {
            if (!profileImageUri.isNullOrBlank()) {
                AsyncImage(
                    model = profileImageUri,
                    contentDescription = "Foto Profilo",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Foto Profilo di default",
                    modifier = Modifier.size(60.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        SmallFloatingActionButton(
            onClick = { showMenu = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(32.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Modifica foto",
                modifier = Modifier.size(16.dp)
            )
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text("Seleziona nuova foto") },
                onClick = {
                    showMenu = false
                    onPickImage()
                }
            )
            if (!profileImageUri.isNullOrBlank()) {
                DropdownMenuItem(
                    text = { Text("Rimuovi foto", color = MaterialTheme.colorScheme.error) },
                    onClick = {
                        showMenu = false
                        onRemoveImage()
                    }
                )
            }
        }
    }
}

@Composable
private fun CosmeticsGrid(
    ownedCosmetics: List<OwnedCosmetic>,
    modifier: Modifier = Modifier
) {
    if (ownedCosmetics.isEmpty()) {
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Non hai ancora acquistato cosmetici.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = modifier,
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(ownedCosmetics) { cosmetic ->
                CosmeticItem(itemId = cosmetic.itemId)
            }
        }
    }
}

@Composable
fun StatItem(
    icon: ImageVector,
    value: String,
    label: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(32.dp)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.SansSerif
        )

        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun CoinStatItem(
    value: String,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            painter = painterResource(id = R.drawable.coin),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(40.dp)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.SansSerif
        )

        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun CosmeticItem(itemId: String) {
    Card(
        modifier = Modifier.aspectRatio(1f),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = itemId,
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 4.dp)
            )
        }
    }
}