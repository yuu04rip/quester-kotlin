package com.example.quester.ui.screens.customization

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quester.ui.components.AvatarCosmetics
import com.example.quester.ui.components.AvatarView
import com.example.quester.ui.components.FrameType
import com.example.quester.ui.components.HatType
import com.example.quester.ui.components.WeaponType
import com.example.quester.ui.screens.FantasyBackground
import com.example.quester.ui.screens.FantasyGold
import com.example.quester.ui.screens.FantasyGoldLight
import com.example.quester.ui.screens.FantasySurface
import com.example.quester.ui.screens.FantasyText
import com.example.quester.ui.screens.FantasyTextSecondary

@Composable
fun AvatarCustomizationScreen(
    onBack: () -> Unit,
    onSave: (AvatarCosmetics) -> Unit
) {
    val customizationState = rememberCustomizationState()

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomizationHeader(onBack = onBack)

            Spacer(modifier = Modifier.height(16.dp))

            AvatarPreviewCard(
                cosmetics = AvatarCosmetics(
                    hat = customizationState.selectedHat,
                    weapon = customizationState.selectedWeapon,
                    frame = customizationState.selectedFrame
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            CustomizationSections(state = customizationState)

            Spacer(modifier = Modifier.height(24.dp))

            CustomizationActions(
                onReset = {
                    customizationState.selectedHat = HatType.NONE
                    customizationState.selectedWeapon = WeaponType.NONE
                    customizationState.selectedFrame = FrameType.NONE
                },
                onSave = {
                    onSave(
                        AvatarCosmetics(
                            hat = customizationState.selectedHat,
                            weapon = customizationState.selectedWeapon,
                            frame = customizationState.selectedFrame
                        )
                    )
                }
            )
        }
    }
}

// ===== STATE CLASS =====

@Stable
private class CustomizationState {
    var selectedHat by mutableStateOf(HatType.NONE)
    var selectedWeapon by mutableStateOf(WeaponType.NONE)
    var selectedFrame by mutableStateOf(FrameType.NONE)
}

@Composable
private fun rememberCustomizationState(): CustomizationState {
    return remember {
        CustomizationState()
    }
}

// ===== HEADER =====

@Composable
private fun CustomizationHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Text(
                text = "✕",
                color = FantasyText,
                fontSize = 24.sp
            )
        }

        Text(
            text = "✦ Personalizza Avatar ✦",
            color = FantasyGoldLight,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Box(modifier = Modifier.size(48.dp))
    }
}

// ===== AVATAR PREVIEW =====

@Composable
private fun AvatarPreviewCard(cosmetics: AvatarCosmetics) {
    Card(
        modifier = Modifier
            .size(220.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(24.dp)
            )
            .border(
                width = 2.dp,
                color = FantasyGold.copy(alpha = 0.5f),
                shape = RoundedCornerShape(24.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = FantasySurface.copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AvatarView(
                cosmetics = cosmetics,
                size = 180,
                isEditable = false
            )
        }
    }
}

// ===== CUSTOMIZATION SECTIONS =====

@Composable
private fun CustomizationSections(state: CustomizationState) {
    // Sezione Testa
    CustomizationSection(
        title = "Copricapo",
        options = HatType.entries.map { it.displayName to it },
        selectedOption = state.selectedHat,
        onOptionSelected = { state.selectedHat = it }
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Sezione Arma
    CustomizationSection(
        title = "Arma",
        options = WeaponType.entries.map { it.displayName to it },
        selectedOption = state.selectedWeapon,
        onOptionSelected = { state.selectedWeapon = it }
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Sezione Cornice
    CustomizationSection(
        title = "Cornice",
        options = FrameType.entries.map { it.displayName to it },
        selectedOption = state.selectedFrame,
        onOptionSelected = { state.selectedFrame = it }
    )
}

// ===== SINGLE SECTION =====

@Composable
private fun <T> CustomizationSection(
    title: String,
    options: List<Pair<String, T>>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = FantasySurface.copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = title,
                color = FantasyGoldLight,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                options.forEach { (displayName, option) ->
                    val isSelected = option == selectedOption

                    CustomizationOptionChip(
                        displayName = displayName,
                        isSelected = isSelected,
                        onClick = { onOptionSelected(option) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// ===== OPTION CHIP =====

@Composable
private fun CustomizationOptionChip(
    displayName: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .border(
                width = 2.dp,
                color = if (isSelected) FantasyGold else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                color = if (isSelected) FantasyGold.copy(alpha = 0.2f) else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = displayName,
            color = if (isSelected) FantasyGoldLight else FantasyTextSecondary,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

// ===== ACTIONS =====

@Composable
private fun CustomizationActions(
    onReset: () -> Unit,
    onSave: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onReset,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3A1E24),
                contentColor = FantasyTextSecondary
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Ripristina")
        }

        Button(
            onClick = onSave,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = FantasyGold,
                contentColor = Color(0xFF0D0B14)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Salva",
                fontWeight = FontWeight.Bold
            )
        }
    }
}