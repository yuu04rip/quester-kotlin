package com.example.quester.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.quester.R
import com.example.quester.data.model.OwnedCosmetic
import com.example.quester.data.model.User
import com.example.quester.ui.components.AvatarCosmetics
import com.example.quester.ui.components.AvatarView
import com.example.quester.ui.components.DeleteAccountDialog
import com.example.quester.ui.components.EditUsernameDialog
import com.example.quester.ui.components.DynamicTitle
import com.example.quester.ui.components.MagicBurstButton
import com.example.quester.ui.components.RegalCrownBadge
import com.example.quester.ui.components.RoyalBackground
import com.example.quester.ui.theme.AppTheme
import com.example.quester.ui.theme.getButtonStyle
import com.example.quester.utils.CosmeticIdMapper

data class ProfileXpData(
    val xpProgress: Float,
    val xpInCurrentLevel: Int,
    val xpNeededForLevel: Int,
    val levelUpCoins: Int
)

data class ProfileUiState(
    val showEditUsername: Boolean = false,
    val showDeleteAccount: Boolean = false,
    val usernameError: String? = null
)

data class ProfileActions(
    val onLogout: () -> Unit,
    val onDeleteAccount: () -> Unit,
    val onUpdateUsername: (String) -> Unit,
    val onShowEditUsername: () -> Unit,
    val onShowDeleteAccount: () -> Unit,
    val onHideDialogs: () -> Unit,
    val onShowCustomization: () -> Unit,
    val onThemeApplied: (AppTheme) -> Unit = {},
    val onSaveCosmetics: (AvatarCosmetics) -> Unit = {}
)

@Composable
fun ProfileContent(
    user: User,
    ownedCosmetics: List<OwnedCosmetic>,
    xpData: ProfileXpData,
    uiState: ProfileUiState,
    actions: ProfileActions
) {
    val equippedCosmetics = remember(user.equippedHat, user.equippedWeapon, user.equippedFrame) {
        AvatarCosmetics(
            hat = CosmeticIdMapper.parseHatType(user.equippedHat),
            weapon = CosmeticIdMapper.parseWeaponType(user.equippedWeapon),
            frame = CosmeticIdMapper.parseFrameType(user.equippedFrame)
        )
    }

    RoyalBackground {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 20.dp, bottom = 20.dp)
        ) {
            item {
                ProfileCard(
                    user = user,
                    equippedCosmetics = equippedCosmetics,
                    xpData = xpData,
                    onShowEditUsername = actions.onShowEditUsername,
                    onShowCustomization = actions.onShowCustomization
                )
            }

            item {
                OwnedCosmeticsSection(
                    ownedCosmetics = ownedCosmetics,
                    onRefresh = {},
                    onThemeApplied = actions.onThemeApplied,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                MagicBurstButton(
                    text = "ESCI DAL REGNO",
                    loading = false,
                    onClickAfterEffect = actions.onLogout,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                val buttonStyle = getButtonStyle()
                OutlinedButton(
                    onClick = actions.onShowDeleteAccount,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = buttonStyle.getOutlinedButtonColors(),
                    shape = buttonStyle.getButtonShape()
                ) {
                    Text(
                        text = "Lascia il Regno",
                        style = buttonStyle.getTextStyle().copy(
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                        )
                    )
                }
            }
        }
    }

    ProfileDialogs(
        user = user,
        uiState = uiState,
        actions = actions
    )
}

@Composable
private fun ProfileDialogs(
    user: User,
    uiState: ProfileUiState,
    actions: ProfileActions
) {
    if (uiState.showEditUsername) {
        EditUsernameDialog(
            currentUsername = user.username,
            onDismiss = actions.onHideDialogs,
            onConfirm = { newUsername ->
                actions.onUpdateUsername(newUsername)
                actions.onHideDialogs()
            },
            error = uiState.usernameError
        )
    }

    if (uiState.showDeleteAccount) {
        DeleteAccountDialog(
            onDismiss = actions.onHideDialogs,
            onConfirm = {
                actions.onDeleteAccount()
                actions.onHideDialogs()
            }
        )
    }
}

@Composable
private fun ProfileCard(
    user: User,
    equippedCosmetics: AvatarCosmetics,
    xpData: ProfileXpData,
    onShowEditUsername: () -> Unit,
    onShowCustomization: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 24.dp, shape = RoundedCornerShape(28.dp))
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.65f),
                shape = RoundedCornerShape(28.dp)
            )
            .padding(2.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f),
                shape = RoundedCornerShape(26.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f)
        ),
        shape = RoundedCornerShape(26.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.TopCenter
            ) {
                AvatarView(
                    modifier = Modifier,
                    cosmetics = equippedCosmetics,
                    size = 200.dp,
                    scale = 2f,
                    verticalOffset = 4.dp,
                    isEditable = true,
                    onClick = onShowCustomization
                )

                RegalCrownBadge(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = (-20).dp),
                    isUnlocked = user.livello >= 50
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            ProfileHeader(
                username = user.username,
                onShowEditUsername = onShowEditUsername
            )

            Surface(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                )
            ) {
                Text(
                    text = "Livello ${user.livello}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            FantasyXpProgress(
                actualLevel = user.livello,
                xpInCurrentLevel = xpData.xpInCurrentLevel,
                xpForCurrentLevel = xpData.xpNeededForLevel,
                progress = xpData.xpProgress
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (user.livello >= 50) "Livello Massimo Raggiunto! 👑" else "Prossimo level-up: +${xpData.levelUpCoins} monete",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    icon = Icons.Default.Star,
                    value = "${user.xpTotale}",
                    label = "XP TOTALI",
                    color = MaterialTheme.colorScheme.secondary
                )

                CoinItem(
                    value = "${user.coins}"
                )
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    username: String,
    onShowEditUsername: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        DynamicTitle(
            text = "✦ $username ✦",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Cambia nome",
            tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f),
            modifier = Modifier
                .size(20.dp)
                .clickable { onShowEditUsername() }
        )
    }
}

@Composable
private fun StatItem(
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
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CoinItem(
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            painter = painterResource(id = R.drawable.coin),
            contentDescription = "Monete",
            modifier = Modifier.size(28.dp),
            tint = Color.Unspecified
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFFFFD700)
        )
        Text(
            text = "MONETE",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}