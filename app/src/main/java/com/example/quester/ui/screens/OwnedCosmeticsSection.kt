package com.example.quester.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quester.data.model.OwnedCosmetic
import kotlin.math.ceil

/*
 * DIMENSIONI
 */
private val COSMETIC_SIZE = 100.dp
private val COSMETIC_SPACING = 10.dp

/*
 * 3 cosmetici per riga
 */
private const val COSMETICS_PER_ROW = 3

@Composable
fun OwnedCosmeticsSection(
    ownedCosmetics: List<OwnedCosmetic>,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {

    val rowCount = if (ownedCosmetics.isEmpty()) {
        0
    } else {
        ceil(
            ownedCosmetics.size.toDouble() / COSMETICS_PER_ROW
        ).toInt()
    }

    val gridHeight = if (rowCount > 0) {
        (COSMETIC_SIZE * rowCount) +
                (COSMETIC_SPACING * (rowCount - 1))
    } else {
        60.dp
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 14.dp,
                shape = RoundedCornerShape(22.dp)
            )
            .border(
                width = 2.dp,
                color = FantasyPurple.copy(alpha = 0.65f),
                shape = RoundedCornerShape(22.dp)
            )
            .border(
                width = 1.dp,
                color = FantasyGold.copy(alpha = 0.35f),
                shape = RoundedCornerShape(20.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = FantasySurface.copy(alpha = 0.97f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            /*
             * HEADER
             */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {

                    Text(
                        text = "I Tuoi Cosmetici",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = FantasyGoldLight,
                        fontFamily = FontFamily.Serif
                    )

                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )

                    Text(
                        text = "${ownedCosmetics.size} oggetti posseduti",
                        style = MaterialTheme.typography.labelMedium,
                        color = FantasyTextSecondary
                    )
                }

                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier.size(36.dp)
                ) {

                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Aggiorna cosmetici",
                        tint = FantasyGold,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            HorizontalDivider(
                color = FantasyGold.copy(alpha = 0.35f)
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            /*
             * GRIGLIA
             */
            if (ownedCosmetics.isEmpty()) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = FantasyGold.copy(alpha = 0.45f),
                            modifier = Modifier.size(28.dp)
                        )

                        Spacer(
                            modifier = Modifier.height(6.dp)
                        )

                        Text(
                            text = "Nessun cosmetico acquistato",
                            style = MaterialTheme.typography.bodyMedium,
                            color = FantasyTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }

            } else {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(gridHeight),
                    verticalArrangement = Arrangement.spacedBy(
                        COSMETIC_SPACING
                    )
                ) {

                    ownedCosmetics
                        .chunked(COSMETICS_PER_ROW)
                        .forEach { rowItems ->

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(
                                    COSMETIC_SPACING
                                )
                            ) {

                                rowItems.forEach { cosmetic ->

                                    FantasyCosmeticItem(
                                        itemId = cosmetic.itemId
                                    )
                                }

                                /*
                                 * Mantiene le colonne allineate
                                 * nell'ultima riga.
                                 */
                                repeat(
                                    COSMETICS_PER_ROW - rowItems.size
                                ) {

                                    Spacer(
                                        modifier = Modifier.size(
                                            COSMETIC_SIZE
                                        )
                                    )
                                }
                            }
                        }
                }
            }
        }
    }
}

@Composable
private fun FantasyCosmeticItem(
    itemId: String
) {

    val shape = RoundedCornerShape(16.dp)

    Card(
        modifier = Modifier
            .size(COSMETIC_SIZE)
            .shadow(
                elevation = 8.dp,
                shape = shape
            )
            .border(
                width = 2.dp,
                color = FantasyPurple.copy(alpha = 0.8f),
                shape = shape
            )
            .border(
                width = 1.dp,
                color = FantasyGold.copy(alpha = 0.45f),
                shape = RoundedCornerShape(14.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        shape = shape
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(COSMETIC_SIZE)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            FantasySurfaceLight,
                            FantasyPurpleDark.copy(alpha = 0.85f),
                            Color(0xFF120D1C)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {

            /*
             * BAGLIORE CENTRALE
             */
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .background(
                        FantasyPurple.copy(alpha = 0.14f),
                        RoundedCornerShape(50)
                    )
                    .border(
                        width = 1.dp,
                        color = FantasyGold.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(50)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = FantasyGold.copy(alpha = 0.9f),
                    modifier = Modifier.size(32.dp)
                )
            }

            /*
             * CODICE COSMETICO
             */
            Text(
                text = itemId
                    .take(3)
                    .uppercase(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = FantasyText,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier
                    .align(Alignment.Center)
            )

            /*
             * PICCOLA DECORAZIONE SUPERIORE
             */
            Text(
                text = "✦",
                color = FantasyGoldLight,
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 5.dp)
            )

            /*
             * ID COMPLETO
             */
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Color.Black.copy(alpha = 0.35f)
                    )
                    .padding(
                        vertical = 4.dp,
                        horizontal = 5.dp
                    )
            ) {

                Text(
                    text = itemId,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 7.sp,
                    color = FantasyTextSecondary.copy(alpha = 0.85f),
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}