package com.example.quester.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.quester.R

@Composable
fun ProfileAvatar(
    profileImageUri: String?,
    onPickImage: () -> Unit,
    onRemoveImage: () -> Unit
) {

    var showMenu by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier.size(120.dp),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .size(120.dp)
                .border(
                    2.dp,
                    FantasyGold.copy(
                        alpha = 0.8f
                    ),
                    CircleShape
                )
                .border(
                    1.dp,
                    FantasyPurple.copy(
                        alpha = 0.5f
                    ),
                    CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(FantasyPurpleDark)
                .border(
                    1.dp,
                    FantasyGoldLight,
                    CircleShape
                )
                .clickable {
                    showMenu = true
                },

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

        FloatingActionButton(
            onClick = {
                showMenu = true
            },

            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(36.dp),

            containerColor = FantasyGold,
            contentColor = Color(0xFF0D0B14),
            shape = CircleShape,

            elevation = FloatingActionButtonDefaults.elevation(
                4.dp
            )
        ) {

            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Modifica foto",
                modifier = Modifier.size(16.dp)
            )
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = {
                showMenu = false
            },
            containerColor = FantasySurface
        ) {

            DropdownMenuItem(
                text = {
                    Text(
                        "Seleziona nuova foto",
                        color = FantasyText
                    )
                },

                onClick = {
                    showMenu = false
                    onPickImage()
                }
            )

            if (!profileImageUri.isNullOrBlank()) {

                DropdownMenuItem(
                    text = {
                        Text(
                            "Rimuovi foto",
                            color = Color(0xFFE57373)
                        )
                    },

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
fun FantasyXpProgress(
    xpTotale: Int
    // Parametro 'livello' rimosso perché non utilizzato
    // Il livello viene calcolato automaticamente da xpTotale
) {
    // Usa la funzione globale per calcolare il livello effettivo
    val actualLevel = calculateLevelFromXp(xpTotale)

    // Calcola l'XP necessario per il livello effettivo
    val xpForCurrentLevel = getXpRequiredForLevel(actualLevel)

    // Calcola l'XP nel livello corrente usando la funzione globale
    val xpInCurrentLevel = getXpInCurrentLevel(xpTotale, actualLevel)

    // Calcola il progresso usando la funzione globale
    val progress = getXpProgress(xpTotale, actualLevel)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = "Liv. $actualLevel",
                style = MaterialTheme.typography.labelSmall,
                color = FantasyTextSecondary,
                fontFamily = FontFamily.Serif
            )

            Text(
                text = "$xpInCurrentLevel / $xpForCurrentLevel XP",
                style = MaterialTheme.typography.labelSmall,
                color = FantasyTextSecondary,
                fontFamily = FontFamily.Serif
            )

            Text(
                text = "Liv. ${actualLevel + 1}",
                style = MaterialTheme.typography.labelSmall,
                color = FantasyTextSecondary,
                fontFamily = FontFamily.Serif
            )
        }

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(
                    RoundedCornerShape(6.dp)
                )
                .background(
                    FantasyPurpleDark.copy(
                        alpha = 0.5f
                    )
                )
                .border(
                    1.dp,
                    FantasyGold.copy(
                        alpha = 0.3f
                    ),
                    RoundedCornerShape(6.dp)
                )
        ) {

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .clip(
                        RoundedCornerShape(6.dp)
                    )
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

@Composable
fun FantasyStatItem(
    icon: ImageVector,
    value: String,
    label: String,
    color: Color
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

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
    value: String
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            painter = painterResource(
                id = R.drawable.coin
            ),
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
            text = "MONETE",
            style = MaterialTheme.typography.labelMedium,
            color = FantasyTextSecondary,
            fontFamily = FontFamily.Serif
        )
    }
}