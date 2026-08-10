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
import com.example.quester.ui.components.FantasyTitle
import kotlin.math.ceil

/*
 * Dimensioni
 */
private val COSMETIC_SIZE = 100.dp
private val COSMETIC_SPACING = 10.dp
private const val COSMETICS_PER_ROW = 3

@Composable
fun OwnedCosmeticsSection(
    modifier: Modifier = Modifier,
    ownedCosmetics: List<OwnedCosmetic>,
    onRefresh: () -> Unit = {}
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
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.65f),
                shape = RoundedCornerShape(22.dp)
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f),
                shape = RoundedCornerShape(20.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.97f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            // HEADER con FantasyTitle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {

                    FantasyTitle(
                        text = "I Tuoi Cosmetici",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )

                    Text(
                        text = "${ownedCosmetics.size} oggetti posseduti",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = FontFamily.SansSerif
                    )
                }

                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier.size(36.dp)
                ) {

                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Aggiorna cosmetici",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f)
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            // Griglia
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
                            tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.45f),
                            modifier = Modifier.size(28.dp)
                        )

                        Spacer(
                            modifier = Modifier.height(6.dp)
                        )

                        Text(
                            text = "Nessun cosmetico acquistato",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = FontFamily.SansSerif,
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

                                    CosmeticItem(
                                        itemId = cosmetic.itemId
                                    )
                                }

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
private fun CosmeticItem(
    itemId: String
) {

    val shape = RoundedCornerShape(12.dp)

    Card(
        modifier = Modifier
            .size(COSMETIC_SIZE)
            .shadow(
                elevation = 4.dp,
                shape = shape
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                shape = shape
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        shape = shape
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(COSMETIC_SIZE),
            contentAlignment = Alignment.Center
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                // Icona cosmetico (simbolo generico)
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Nome cosmetico formattato con font fantasy
                Text(
                    text = formatCosmeticName(itemId),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontFamily = FontFamily.Serif
                    ),
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                )
            }
        }
    }
}

/**
 * Formatta l'ID del cosmetico in un nome leggibile
 * Es: "frame_mago" → "Frame Mago"
 *     "skin_slime" → "Skin Slime"
 */
private fun formatCosmeticName(itemId: String): String {
    return itemId
        .split("_")
        .joinToString(" ") { word ->
            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
}