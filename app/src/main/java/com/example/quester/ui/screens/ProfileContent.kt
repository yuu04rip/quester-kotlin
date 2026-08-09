package com.example.quester.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.quester.data.model.OwnedCosmetic
import com.example.quester.data.model.User
import com.example.quester.ui.components.MagicBurstButton

@Composable
fun ProfileContent(
    user: User,
    ownedCosmetics: List<OwnedCosmetic>,
    onPickImage: () -> Unit,
    onRemoveImage: () -> Unit,
    onLogout: () -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.spacedBy(
            16.dp
        ),

        contentPadding = PaddingValues(
            top = 20.dp,
            bottom = 20.dp
        )
    ) {

        item {
            ProfileCard(
                user = user,
                onPickImage = onPickImage,
                onRemoveImage = onRemoveImage
            )
        }

        item {
            OwnedCosmeticsSection(
                ownedCosmetics = ownedCosmetics,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            MagicBurstButton(
                text = "ESCI DAL REGNO",
                loading = false,
                onClickAfterEffect = onLogout,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ProfileCard(
    user: User,
    onPickImage: () -> Unit,
    onRemoveImage: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                24.dp,
                RoundedCornerShape(28.dp)
            )
            .border(
                2.dp,
                FantasyPurple.copy(alpha = 0.65f),
                RoundedCornerShape(28.dp)
            )
            .padding(2.dp)
            .border(
                1.dp,
                FantasyGold.copy(alpha = 0.8f),
                RoundedCornerShape(26.dp)
            ),

        colors = CardDefaults.cardColors(
            containerColor = FantasySurface.copy(
                alpha = 0.98f
            )
        ),

        shape = RoundedCornerShape(26.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 24.dp,
                    vertical = 28.dp
                ),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            ProfileAvatar(
                profileImageUri = user.profileImageUri,
                onPickImage = onPickImage,
                onRemoveImage = onRemoveImage
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "✦ ${user.username} ✦",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = FantasyGoldLight,
                fontFamily = FontFamily.Serif
            )

            Surface(
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 4.dp
                ),

                shape = RoundedCornerShape(12.dp),

                color = FantasyPurpleDark.copy(
                    alpha = 0.7f
                ),

                border = BorderStroke(
                    1.dp,
                    FantasyGold.copy(alpha = 0.5f)
                )
            ) {

                Text(
                    text = "Livello ${user.livello}",
                    style = MaterialTheme.typography.titleMedium,
                    color = FantasyGold,
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 4.dp
                    )
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            HorizontalDivider(
                color = FantasyGold.copy(
                    alpha = 0.35f
                )
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // ✅ FIX: Rimosso il parametro 'livello' perché non più utilizzato
            FantasyXpProgress(
                xpTotale = user.xpTotale
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

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
                    value = "${user.coins}"
                )
            }
        }
    }
}