package com.example.quester.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quester.R

/**
 * Tipi di cosmetici per l'avatar
 *
 * NOTA: I campi iconResource e iconColor verranno utilizzati per caricare immagini PNG
 * quando saranno disponibili. Per ora utilizziamo solo displayName.
 */
enum class HatType(val displayName: String, val iconResource: Int? = null) {
    NONE("Nessuno", null),
    MAGO("Cappello del Mago", null),
    SCI_FI("Visore Futuristico", null)
}

enum class WeaponType(val displayName: String, val iconResource: Int? = null) {
    NONE("Nessuna", null),
    STAFF("Bastone del Mago", null),
    SWORD("Spada del Cavaliere", null),
    GUN("Pistola Spaziale", null)
}

enum class FrameType(val displayName: String, val color: Color) {
    NONE("Nessuna", Color.Transparent),
    MAGO("Cornice del Mago", Color(0xFF6B4C9A)),
    CAVALIERE("Cornice del Cavaliere", Color(0xFFD4A84F)),
    SCI_FI("Cornice Sci-Fi", Color(0xFF00BCD4))
}

data class AvatarCosmetics(
    val hat: HatType = HatType.NONE,
    val weapon: WeaponType = WeaponType.NONE,
    val frame: FrameType = FrameType.NONE
)

@Composable
fun AvatarView(
    modifier: Modifier = Modifier,
    cosmetics: AvatarCosmetics = AvatarCosmetics(),
    size: Int = 120,
    onClick: () -> Unit = {},
    isEditable: Boolean = false
) {
    val avatarSize = size.dp
    val innerSize = (size - 20).dp

    Box(
        modifier = modifier
            .size(avatarSize)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .then(
                if (isEditable) Modifier.clickable { onClick() } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        // ===== CORNICE (FRAME) =====
        Box(
            modifier = Modifier
                .size(avatarSize)
                .clip(CircleShape)
                .border(
                    width = 4.dp,
                    color = cosmetics.frame.color,
                    shape = CircleShape
                )
                .background(
                    if (cosmetics.frame != FrameType.NONE) {
                        cosmetics.frame.color.copy(alpha = 0.1f)
                    } else {
                        Color.Transparent
                    }
                )
        )

        // ===== AVATAR BASE =====
        Box(
            modifier = Modifier
                .size(innerSize)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Immagine avatar standard (PNG)
            Icon(
                painter = painterResource(id = R.drawable.ic_avatar_default),
                contentDescription = "Avatar",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier
                    .fillMaxSize()
                    .size((size * 0.6).dp)
            )

            // ===== COSMETICO TESTA (HAT) - VERSIONE TESTO (TEMP) =====
            // TODO: Sostituire con icona PNG quando disponibile
            // if (cosmetics.hat != HatType.NONE && cosmetics.hat.iconResource != null) {
            //     Icon(
            //         painter = painterResource(id = cosmetics.hat.iconResource),
            //         contentDescription = cosmetics.hat.displayName,
            //         tint = Color.Unspecified,
            //         modifier = Modifier
            //             .size((size * 0.3).dp)
            //             .align(Alignment.TopCenter)
            //     )
            // }

            // Versione temporanea con testo
            if (cosmetics.hat != HatType.NONE) {
                Text(
                    text = "●", // Simbolo temporaneo per indicare il copricapo
                    fontSize = (size * 0.25).sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }

            // ===== COSMETICO ARMA (WEAPON) - VERSIONE TESTO (TEMP) =====
            // TODO: Sostituire con icona PNG quando disponibile
            // if (cosmetics.weapon != WeaponType.NONE && cosmetics.weapon.iconResource != null) {
            //     Icon(
            //         painter = painterResource(id = cosmetics.weapon.iconResource),
            //         contentDescription = cosmetics.weapon.displayName,
            //         tint = Color.Unspecified,
            //         modifier = Modifier
            //             .size((size * 0.25).dp)
            //             .align(Alignment.BottomEnd)
            //     )
            // }

            // Versione temporanea con testo
            if (cosmetics.weapon != WeaponType.NONE) {
                Text(
                    text = "✦", // Simbolo temporaneo per indicare l'arma
                    fontSize = (size * 0.2).sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.BottomEnd)
                )
            }
        }
    }
}

@Composable
fun AvatarPreview(
    cosmetics: AvatarCosmetics = AvatarCosmetics(),
    size: Int = 200,
    modifier: Modifier = Modifier
) {
    AvatarView(
        modifier = modifier,
        cosmetics = cosmetics,
        size = size,
        isEditable = false
    )
}