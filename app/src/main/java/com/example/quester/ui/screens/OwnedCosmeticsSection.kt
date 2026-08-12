package com.example.quester.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quester.R
import com.example.quester.data.model.OwnedCosmetic
import com.example.quester.data.preferences.ThemePreferences
import com.example.quester.ui.components.FantasyTitle
import com.example.quester.ui.theme.AppTheme
import com.example.quester.ui.theme.ThemeManager
import kotlinx.coroutines.launch
import kotlin.math.ceil

/*
 * Dimensioni
 */
private val COSMETIC_SIZE = 100.dp
private val COSMETIC_SPACING = 10.dp
private const val COSMETICS_PER_ROW = 3

private val THEME_IDS = listOf(
    "theme_arcade",
    "theme_fantasy"
)

@Composable
fun OwnedCosmeticsSection(
    modifier: Modifier = Modifier,
    ownedCosmetics: List<OwnedCosmetic>,
    onRefresh: () -> Unit = {},
    onThemeApplied: (AppTheme) -> Unit = {}
) {
    val currentTheme by ThemeManager.currentTheme.collectAsState()
    val context = LocalContext.current
    val themePreferences = ThemePreferences(context)
    val scope = rememberCoroutineScope()

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
            CosmeticCardHeader(
                itemCount = ownedCosmetics.size,
                onRefresh = onRefresh
            )

            Spacer(modifier = Modifier.height(10.dp))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f)
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (ownedCosmetics.isEmpty()) {
                EmptyCosmeticsView()
            } else {
                CosmeticsGrid(
                    ownedCosmetics = ownedCosmetics,
                    currentTheme = currentTheme,
                    onThemeApplied = { newTheme ->
                        ThemeManager.setTheme(newTheme)
                        scope.launch {
                            themePreferences.saveTheme(newTheme)
                        }
                        onThemeApplied(newTheme)
                    }
                )
            }
        }
    }
}

@Composable
private fun CosmeticCardHeader(
    itemCount: Int,
    onRefresh: () -> Unit
) {
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

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "$itemCount oggetti posseduti",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
}

@Composable
private fun EmptyCosmeticsView() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.45f),
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Nessun cosmetico acquistato",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CosmeticsGrid(
    ownedCosmetics: List<OwnedCosmetic>,
    currentTheme: AppTheme,
    onThemeApplied: (AppTheme) -> Unit
) {
    val gridHeight = calculateGridHeight(ownedCosmetics.size)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(gridHeight),
        verticalArrangement = Arrangement.spacedBy(COSMETIC_SPACING)
    ) {
        ownedCosmetics
            .chunked(COSMETICS_PER_ROW)
            .forEach { rowItems ->
                CosmeticRow(
                    rowItems = rowItems,
                    currentTheme = currentTheme,
                    onThemeApplied = onThemeApplied
                )
            }
    }
}

@Composable
private fun CosmeticRow(
    rowItems: List<OwnedCosmetic>,
    currentTheme: AppTheme,
    onThemeApplied: (AppTheme) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(COSMETIC_SPACING)
    ) {
        rowItems.forEach { cosmetic ->
            val isTheme = cosmetic.itemId in THEME_IDS
            val isCurrentTheme = isTheme && isThemeActive(cosmetic.itemId, currentTheme)

            CosmeticItem(
                itemId = cosmetic.itemId,
                isTheme = isTheme,
                isSelected = isCurrentTheme,
                onClick = {
                    if (isTheme) {
                        val nextTheme = getToggledTheme(cosmetic.itemId, currentTheme)
                        onThemeApplied(nextTheme)
                    }
                }
            )
        }

        repeat(COSMETICS_PER_ROW - rowItems.size) {
            Spacer(modifier = Modifier.size(COSMETIC_SIZE))
        }
    }
}

private fun calculateGridHeight(itemCount: Int): Dp {
    val rowCount = if (itemCount == 0) 0 else ceil(itemCount.toDouble() / COSMETICS_PER_ROW).toInt()
    return if (rowCount > 0) {
        (COSMETIC_SIZE * rowCount) + (COSMETIC_SPACING * (rowCount - 1))
    } else {
        60.dp
    }
}

private fun isThemeActive(itemId: String, currentTheme: AppTheme): Boolean {
    return when (itemId) {
        "theme_arcade" -> currentTheme == AppTheme.ARCADE
        "theme_fantasy" -> currentTheme == AppTheme.FANTASY
        else -> false
    }
}

private fun getDefaultToggledTheme(currentTheme: AppTheme): AppTheme {
    return if (currentTheme == AppTheme.ARCADE) AppTheme.FANTASY else AppTheme.ARCADE
}

private fun getToggledTheme(itemId: String, currentTheme: AppTheme): AppTheme {
    if (itemId == "theme_arcade" || itemId == "theme_fantasy") {
        return getDefaultToggledTheme(currentTheme)
    }
    return AppTheme.FANTASY
}

private fun getThemeDisplayName(itemId: String): String {
    return when (itemId) {
        "theme_arcade" -> "Tema Arcade"
        "theme_fantasy" -> "Tema Fantasy"
        else -> formatCosmeticName(itemId)
    }
}

@Composable
private fun CosmeticItem(
    itemId: String,
    isTheme: Boolean = false,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    val shape = RoundedCornerShape(12.dp)
    val displayName = if (isTheme) getThemeDisplayName(itemId) else formatCosmeticName(itemId)

    Card(
        modifier = Modifier
            .size(COSMETIC_SIZE)
            .shadow(
                elevation = if (isSelected) 8.dp else 4.dp,
                shape = shape
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.secondary
                } else {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                },
                shape = shape
            )
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            }
        ),
        shape = shape
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(COSMETIC_SIZE),
            contentAlignment = Alignment.Center
        ) {
            CosmeticCardContent(
                itemId = itemId,
                displayName = displayName,
                isSelected = isSelected,
                isTheme = isTheme
            )
        }
    }
}

// ============================================================
// COSMETIC CONTENT (DINAMICO)
// ============================================================

@Composable
private fun CosmeticCardContent(
    itemId: String,
    displayName: String,
    isSelected: Boolean,
    isTheme: Boolean
) {
    val context = LocalContext.current

    val drawableResId = remember(itemId) {
        val resourceName = "ic_$itemId"
        val resId = context.resources.getIdentifier(resourceName, "drawable", context.packageName)

        if (resId != 0) resId else context.resources.getIdentifier(itemId, "drawable", context.packageName)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (drawableResId != 0) {
            Image(
                painter = painterResource(id = drawableResId),
                contentDescription = displayName,
                modifier = Modifier.size(40.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = displayName,
                tint = if (isSelected) {
                    MaterialTheme.colorScheme.secondary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                },
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = displayName,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) {
                MaterialTheme.colorScheme.secondary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
        )

        if (isSelected && isTheme) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "ATTIVO",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun formatCosmeticName(itemId: String): String {
    return itemId
        .split("_")
        .joinToString(" ") { word ->
            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
}