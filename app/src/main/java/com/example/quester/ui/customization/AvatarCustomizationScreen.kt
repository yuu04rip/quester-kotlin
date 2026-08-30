package com.example.quester.ui.screens.customization

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.quester.ui.components.AvatarCosmetics
import com.example.quester.ui.components.AvatarView
import com.example.quester.ui.components.FrameType
import com.example.quester.ui.components.HatType
import com.example.quester.ui.components.WeaponType
import com.example.quester.utils.CosmeticIdMapper

private const val CORN_PREFIX = "Corn. "

private fun isCosmeticOwned(
    option: Any,
    ownedItemIds: Set<String>
): Boolean {
    if (option == HatType.NONE || option == WeaponType.NONE || option == FrameType.NONE || option == FrameType.BASIC) {
        return true
    }

    val shopId = when (option) {
        is HatType -> CosmeticIdMapper.hatToShopId(option)
        is WeaponType -> CosmeticIdMapper.weaponToShopId(option)
        is FrameType -> CosmeticIdMapper.frameToShopId(option)
        else -> null
    }

    val enumName = (option as? Enum<*>)?.name

    return (shopId != null && shopId in ownedItemIds) ||
            (enumName != null && enumName in ownedItemIds)
}

private fun formatShortName(name: String): String {
    return name
        .replace("Cappello del ", "Cap. ", ignoreCase = true)
        .replace("Cappello ", "Cap. ", ignoreCase = true)
        .replace("Visore ", "Vis. ", ignoreCase = true)
        .replace("Pistola ", "Pist. ", ignoreCase = true)
        .replace("Spada del ", "Spada ", ignoreCase = true)
        .replace("Spada della ", "Spada ", ignoreCase = true)
        .replace("Cornice del ", CORN_PREFIX, ignoreCase = true)
        .replace("Cornice della ", CORN_PREFIX, ignoreCase = true)
        .replace("Cornice ", CORN_PREFIX, ignoreCase = true)
}

@Composable
fun AvatarCustomizationScreen(
    initialCosmetics: AvatarCosmetics = AvatarCosmetics(),
    ownedItemIds: Set<String> = emptySet(),
    onBack: () -> Unit,
    onSave: (AvatarCosmetics) -> Unit
) {
    val customizationState = rememberCustomizationState(initialCosmetics)
    val scrollState = rememberScrollState()

    val view = LocalView.current
    DisposableEffect(Unit) {
        val window = (view.context as? Activity)?.window
        if (window != null) {
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.hide(WindowInsetsCompat.Type.navigationBars())
            insetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        onDispose {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.show(WindowInsetsCompat.Type.navigationBars())
            }
        }
    }

    val currentCosmetics = AvatarCosmetics(
        hat = customizationState.selectedHat,
        weapon = customizationState.selectedWeapon,
        frame = if (customizationState.selectedFrame == FrameType.NONE) FrameType.BASIC else customizationState.selectedFrame
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomizationHeader(onBack = onBack)

            Spacer(modifier = Modifier.height(14.dp))

            AvatarPreviewCard(cosmetics = currentCosmetics)

            Spacer(modifier = Modifier.height(16.dp))

            SelectionSummary(
                hat = customizationState.selectedHat,
                weapon = customizationState.selectedWeapon,
                frame = currentCosmetics.frame
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomizationSections(
                state = customizationState,
                ownedItemIds = ownedItemIds
            )

            Spacer(modifier = Modifier.height(20.dp))

            CustomizationActions(
                onReset = { customizationState.reset(initialCosmetics) },
                onSave = { onSave(currentCosmetics) },
                hasChanges = customizationState.hasChanges(initialCosmetics)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Stable
private class CustomizationState(
    initialCosmetics: AvatarCosmetics = AvatarCosmetics()
) {
    var selectedHat by mutableStateOf(initialCosmetics.hat)
    var selectedWeapon by mutableStateOf(initialCosmetics.weapon)
    var selectedFrame by mutableStateOf(
        if (initialCosmetics.frame == FrameType.NONE) FrameType.BASIC else initialCosmetics.frame
    )

    fun reset(initial: AvatarCosmetics) {
        selectedHat = initial.hat
        selectedWeapon = initial.weapon
        selectedFrame = if (initial.frame == FrameType.NONE) FrameType.BASIC else initial.frame
    }

    fun hasChanges(initial: AvatarCosmetics): Boolean {
        val normalizedInitialFrame = if (initial.frame == FrameType.NONE) FrameType.BASIC else initial.frame
        return selectedHat != initial.hat ||
                selectedWeapon != initial.weapon ||
                selectedFrame != normalizedInitialFrame
    }
}

@Composable
private fun rememberCustomizationState(
    initialCosmetics: AvatarCosmetics
): CustomizationState {
    val state = remember { CustomizationState(initialCosmetics) }

    LaunchedEffect(initialCosmetics) {
        state.reset(initialCosmetics)
    }

    return state
}

@Composable
private fun CustomizationHeader(onBack: () -> Unit) {
    val accentColor = MaterialTheme.colorScheme.secondary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(42.dp)
                .background(
                    color = accentColor.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    width = 1.dp,
                    color = accentColor.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Indietro",
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "PERSONALIZZA",
                color = accentColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Avatar",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Box(modifier = Modifier.size(42.dp))
    }
}

@Composable
private fun AvatarPreviewCard(cosmetics: AvatarCosmetics) {
    val accentColor = MaterialTheme.colorScheme.secondary

    Card(
        modifier = Modifier
            .size(210.dp)
            .shadow(elevation = 16.dp, shape = RoundedCornerShape(26.dp))
            .border(
                width = 1.5.dp,
                color = accentColor.copy(alpha = 0.55f),
                shape = RoundedCornerShape(26.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f)
        ),
        shape = RoundedCornerShape(26.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AvatarView(
                cosmetics = cosmetics,
                size = 300.dp,
                scale = 3f,
                verticalOffset = 6.dp,
                isEditable = false
            )
        }
    }
}

@Composable
private fun SelectionSummary(
    hat: HatType,
    weapon: WeaponType,
    frame: FrameType
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f)
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.18f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SelectionBadge(label = "Copricapo", value = formatShortName(hat.displayName))
            SelectionDivider()
            SelectionBadge(label = "Arma", value = formatShortName(weapon.displayName))
            SelectionDivider()
            SelectionBadge(label = "Cornice", value = formatShortName(frame.displayName))
        }
    }
}

@Composable
private fun SelectionDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(28.dp)
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f))
    )
}

@Composable
private fun SelectionBadge(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier.width(90.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label.uppercase(),
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.7.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
private fun CustomizationSections(
    state: CustomizationState,
    ownedItemIds: Set<String>
) {
    CustomizationSection(
        title = "COPRICAPO",
        options = HatType.entries.map { formatShortName(it.displayName) to it },
        selectedOption = state.selectedHat,
        ownedItemIds = ownedItemIds,
        onOptionSelected = { option ->
            state.selectedHat = if (state.selectedHat == option) HatType.NONE else option
        }
    )

    Spacer(modifier = Modifier.height(10.dp))

    CustomizationSection(
        title = "ARMA",
        options = WeaponType.entries.map { formatShortName(it.displayName) to it },
        selectedOption = state.selectedWeapon,
        ownedItemIds = ownedItemIds,
        onOptionSelected = { option ->
            state.selectedWeapon = if (state.selectedWeapon == option) WeaponType.NONE else option
        }
    )

    Spacer(modifier = Modifier.height(10.dp))

    CustomizationSection(
        title = "CORNICE",
        options = FrameType.entries
            .filter { it != FrameType.NONE }
            .map { formatShortName(it.displayName) to it },
        selectedOption = state.selectedFrame,
        ownedItemIds = ownedItemIds,
        onOptionSelected = { option ->
            state.selectedFrame = option
        }
    )
}

@Composable
private fun <T> CustomizationSection(
    title: String,
    options: List<Pair<String, T>>,
    selectedOption: T,
    ownedItemIds: Set<String>,
    onOptionSelected: (T) -> Unit
) {
    val accentColor = MaterialTheme.colorScheme.secondary

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f)
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = 1.dp,
            color = accentColor.copy(alpha = 0.14f)
        )
    ) {
        Column(modifier = Modifier.padding(vertical = 12.dp)) {
            Text(
                text = title,
                color = accentColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(options) { (displayName, option) ->
                    val isSelected = option == selectedOption
                    val isOwned = isCosmeticOwned(option as Any, ownedItemIds)

                    CustomizationOptionChip(
                        displayName = displayName,
                        isSelected = isSelected,
                        isLocked = !isOwned,
                        onClick = {
                            if (isOwned) {
                                onOptionSelected(option)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CustomizationOptionChip(
    displayName: String,
    isSelected: Boolean,
    isLocked: Boolean,
    onClick: () -> Unit
) {
    val accentColor = MaterialTheme.colorScheme.secondary

    val borderColor = when {
        isSelected -> accentColor
        isLocked -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
    }

    val bgColor = when {
        isSelected -> accentColor.copy(alpha = 0.16f)
        isLocked -> MaterialTheme.colorScheme.surface.copy(alpha = 0.25f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    }

    Box(
        modifier = Modifier
            .width(100.dp)
            .height(44.dp)
            .alpha(if (isLocked) 0.38f else 1f)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(10.dp)
            )
            .background(
                color = bgColor,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(
                enabled = !isLocked,
                onClick = onClick
            )
            .padding(horizontal = 4.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = displayName,
            color = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurface,
            fontSize = 10.sp,
            lineHeight = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

@Composable
private fun CustomizationActions(
    onReset: () -> Unit,
    onSave: () -> Unit,
    hasChanges: Boolean
) {
    val accentColor = MaterialTheme.colorScheme.secondary
    val buttonTextColor = MaterialTheme.colorScheme.onSecondary

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedButton(
            onClick = onReset,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = accentColor),
            border = BorderStroke(width = 1.dp, color = accentColor.copy(alpha = 0.65f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Ripristina",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Button(
            onClick = onSave,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            enabled = hasChanges,
            colors = ButtonDefaults.buttonColors(
                containerColor = accentColor,
                contentColor = buttonTextColor,
                disabledContainerColor = accentColor.copy(alpha = 0.22f),
                disabledContentColor = buttonTextColor.copy(alpha = 0.45f)
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 1.dp
            )
        ) {
            if (hasChanges) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(17.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }

            Text(
                text = "Salva",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}