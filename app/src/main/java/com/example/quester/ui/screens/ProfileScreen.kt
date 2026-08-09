package com.example.quester.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.quester.R
import com.example.quester.data.model.OwnedCosmetic
import com.example.quester.data.model.User
import com.example.quester.data.repository.UserRepository
import com.example.quester.data.session.SessionManager
import com.example.quester.ui.components.MagicBurstButton
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

// Colori Tema Fantasy
private val FantasyBackground = Color(0xFF0D0B14)
private val FantasySurface = Color(0xFF171321)
private val FantasySurfaceLight = Color(0xFF221B2E)
private val FantasyGold = Color(0xFFD4A84F)
private val FantasyGoldLight = Color(0xFFF0CC78)
private val FantasyPurple = Color(0xFF6B4C9A)
private val FantasyPurpleDark = Color(0xFF2B1D42)
private val FantasyText = Color(0xFFF3EBD8)
private val FantasyTextSecondary = Color(0xFFC8BDA8)

// Costanti per il calcolo dei livelli
private const val XP_PER_LEVEL = 100

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

    val onPickImage: () -> Unit = {
        photoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
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
                    listOf(Color(0xFF120C1E), FantasyBackground, Color(0xFF0B0813))
                )
            )
    ) {
        val currentUser = user
        if (currentUser == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = FantasyGold)
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

@Composable
private fun ProfileContent(
    user: User,
    ownedCosmetics: List<OwnedCosmetic>,
    onPickImage: () -> Unit,
    onRemoveImage: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header con Card fantasy
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(24.dp, RoundedCornerShape(28.dp))
                .border(2.dp, FantasyPurple.copy(alpha = 0.65f), RoundedCornerShape(28.dp))
                .padding(2.dp)
                .border(1.dp, FantasyGold.copy(alpha = 0.8f), RoundedCornerShape(26.dp)),
            colors = CardDefaults.cardColors(containerColor = FantasySurface.copy(alpha = 0.98f)),
            shape = RoundedCornerShape(26.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar
                ProfileAvatar(
                    profileImageUri = user.profileImageUri,
                    onPickImage = onPickImage,
                    onRemoveImage = onRemoveImage
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Nome utente con stile fantasy
                Text(
                    text = "✦ ${user.username} ✦",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = FantasyGoldLight,
                    fontFamily = FontFamily.Serif
                )

                // Livello
                Surface(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = FantasyPurpleDark.copy(alpha = 0.7f),
                    border = BorderStroke(1.dp, FantasyGold.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "⚔ Livello ${user.livello} ⚔",
                        style = MaterialTheme.typography.titleMedium,
                        color = FantasyGold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = FantasyGold.copy(alpha = 0.35f))
                Spacer(modifier = Modifier.height(16.dp))

                // Statistiche con barra XP
                FantasyXpProgress(
                    xpTotale = user.xpTotale,
                    livello = user.livello
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Statistiche
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FantasyStatItem(
                        icon = Icons.Default.Star,
                        value = "${user.xpTotale}",
                        label = "XP TOTALI",
                        color = FantasyGold
                    )
                    FantasyCoinStatItem(
                        value = "${user.coins}",
                        label = "MONETE"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Cosmetici
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .shadow(16.dp, RoundedCornerShape(20.dp))
                .border(2.dp, FantasyPurple.copy(alpha = 0.65f), RoundedCornerShape(20.dp))
                .padding(2.dp)
                .border(1.dp, FantasyGold.copy(alpha = 0.8f), RoundedCornerShape(18.dp)),
            colors = CardDefaults.cardColors(containerColor = FantasySurface.copy(alpha = 0.95f)),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "✦ I Tuoi Cosmetici ✦",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = FantasyGoldLight,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = FantasyGold.copy(alpha = 0.35f))
                Spacer(modifier = Modifier.height(8.dp))

                CosmeticsGrid(
                    ownedCosmetics = ownedCosmetics,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Pulsante Logout
        MagicBurstButton(
            text = "ESCI DAL REGNO",
            loading = false,
            onClickAfterEffect = onLogout,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ===== BARRA DI PROGRESSO XP =====

@Composable
private fun FantasyXpProgress(
    xpTotale: Int,
    livello: Int
) {
    val xpForCurrentLevel = (livello - 1) * XP_PER_LEVEL
    val xpInCurrentLevel = xpTotale - xpForCurrentLevel
    val progress = xpInCurrentLevel.toFloat() / XP_PER_LEVEL

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Liv. ${livello}",
                style = MaterialTheme.typography.labelSmall,
                color = FantasyTextSecondary,
                fontFamily = FontFamily.Serif
            )
            Text(
                text = "$xpInCurrentLevel / $XP_PER_LEVEL XP",
                style = MaterialTheme.typography.labelSmall,
                color = FantasyTextSecondary,
                fontFamily = FontFamily.Serif
            )
            Text(
                text = "Liv. ${livello + 1}",
                style = MaterialTheme.typography.labelSmall,
                color = FantasyTextSecondary,
                fontFamily = FontFamily.Serif
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(FantasyPurpleDark.copy(alpha = 0.5f))
                .border(1.dp, FantasyGold.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                FantasyPurple,
                                FantasyGold
                            )
                        )
                    )
            )
        }
    }
}

// ===== FUNZIONI DI CALCOLO =====

private fun getXpToNextLevel(xpTotale: Int, livello: Int): String {
    val xpForNextLevel = livello * XP_PER_LEVEL
    val xpRemaining = xpForNextLevel - xpTotale
    return if (xpRemaining > 0) "$xpRemaining" else "MAX"
}

// ===== AVATAR =====

@Composable
private fun ProfileAvatar(
    profileImageUri: String?,
    onPickImage: () -> Unit,
    onRemoveImage: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.size(120.dp),
        contentAlignment = Alignment.Center
    ) {
        // Anello decorativo
        Box(
            modifier = Modifier
                .size(120.dp)
                .border(2.dp, FantasyGold.copy(alpha = 0.8f), CircleShape)
                .border(1.dp, FantasyPurple.copy(alpha = 0.5f), CircleShape)
        )

        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(FantasyPurpleDark)
                .border(1.dp, FantasyGoldLight, CircleShape)
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
                    modifier = Modifier.size(50.dp),
                    tint = FantasyGoldLight
                )
            }
        }

        // Pulsante modifica
        FloatingActionButton(
            onClick = { showMenu = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(36.dp),
            containerColor = FantasyGold,
            contentColor = Color(0xFF0D0B14),
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Modifica foto",
                modifier = Modifier.size(16.dp)
            )
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
            containerColor = FantasySurfaceLight
        ) {
            DropdownMenuItem(
                text = { Text("Seleziona nuova foto", color = FantasyText) },
                onClick = {
                    showMenu = false
                    onPickImage()
                }
            )
            if (!profileImageUri.isNullOrBlank()) {
                DropdownMenuItem(
                    text = { Text("Rimuovi foto", color = Color(0xFFE57373)) },
                    onClick = {
                        showMenu = false
                        onRemoveImage()
                    }
                )
            }
        }
    }
}

// ===== COSMETICI =====

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
                text = "✦ Nessun cosmetico acquistato ✦",
                style = MaterialTheme.typography.bodyMedium,
                color = FantasyTextSecondary,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = modifier,
            contentPadding = PaddingValues(bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(ownedCosmetics) { cosmetic ->
                FantasyCosmeticItem(itemId = cosmetic.itemId)
            }
        }
    }
}

// ===== STAT ITEMS =====

@Composable
fun FantasyStatItem(
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
            modifier = Modifier.size(28.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = FantasyText,
            fontFamily = FontFamily.Serif
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = FantasyTextSecondary,
            fontFamily = FontFamily.Serif
        )
    }
}

@Composable
fun FantasyCoinStatItem(
    value: String,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            painter = painterResource(id = R.drawable.coin),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(32.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = FantasyGold,
            fontFamily = FontFamily.Serif
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = FantasyTextSecondary,
            fontFamily = FontFamily.Serif
        )
    }
}

// ===== COSMETIC ITEM =====

@Composable
fun FantasyCosmeticItem(itemId: String) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .border(1.dp, FantasyPurple.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = FantasySurfaceLight.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = Icons.Default.Circle,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = Color(0xFF6B4C9A).copy(alpha = 0.3f)
            )
            Text(
                text = itemId.take(3).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = FantasyGold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(4.dp)
            )
            Text(
                text = itemId,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 8.sp,
                color = FantasyTextSecondary.copy(alpha = 0.6f),
                fontFamily = FontFamily.Monospace,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 4.dp)
            )
        }
    }
}