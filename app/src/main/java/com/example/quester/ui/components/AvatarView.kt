package com.example.quester.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quester.R
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

enum class HatType(val displayName: String, val iconResource: Int? = null) {
    NONE("Nessuno", null),
    MAGO("Cappello del Mago", null),
    ELMO_CAVALIERE("Elmo del Cavaliere", null),
    VISOR_FUTURISTICO("Visore Futuristico", null)
}

enum class WeaponType(val displayName: String, val iconResource: Int? = null) {
    NONE("Nessuna", null),
    STAFF("Bastone del Mago", null),
    SWORD("Spada del Cavaliere", null),
    GUN("Pistola Spaziale", null)
}

// Colori Fissi e Indipendenti da MaterialTheme per le cornici
enum class FrameType(val displayName: String, val color: Color) {
    NONE("Nessuna", Color.Transparent),
    MAGO("Cornice del Mago", Color(0xFF6B4C9A)),      // Viola Mago fisso
    CAVALIERE("Cornice del Cavaliere", Color(0xFFD4A84F)), // Oro Cavaliere fisso
    SCI_FI("Cornice Sci-Fi", Color(0xFF00FF66))        // Verde Neon Sci-Fi
}

data class AvatarCosmetics(
    val hat: HatType = HatType.NONE,
    val weapon: WeaponType = WeaponType.NONE,
    val frame: FrameType = FrameType.NONE
)

// Palette di colori FISSI per l'Avatar
private object AvatarFixedColors {
    val ContainerBackground = Color(0xFF1E1B2E)
    val AvatarSurface = Color(0xFF2B283A)
    val AvatarBorder = Color(0xFF44405A)
    val AvatarIconTint = Color(0xFFA5A1B8)
    val NeonGreenGlow = Color(0xFF00FF66)
    val NeonGreenCore = Color(0xFFE0FFEC)
}

// Struttura dati per le particelle neon
private data class ParticleData(
    val baseAngle: Float,        // Angolo radiale di partenza (radianti)
    val radiusFactor: Float,     // Distanza dal centro (estesa oltre i bordi)
    val size: Float,             // Dimensione della particella
    val alphaOffset: Float,      // Offset temporale per la pulsazione
    val speedFactor: Float,      // Velocità di orbita
    val isPixel: Boolean         // Quadratino Arcade o Pallino Neon
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
    val innerSize = (size - 24).dp

    // ===== ANIMAZIONI ARCADE / NEON =====
    val infiniteTransition = rememberInfiniteTransition(label = "ArcadeFrameTransition")

    // 1. Rotazione continua slow-motion
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ArcadeRotation"
    )

    // 2. Pulsazione ritmica Neon (Effetto respiro/glow)
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ArcadePulse"
    )

    // 3. Avanzamento particelle
    val particleProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ParticleProgress"
    )

    // NOTA: Rimossa la clip esterna per consentire al Bagliore Neon e alle Particelle di strabordare
    Box(
        modifier = modifier
            .size(avatarSize)
            .then(if (isEditable) Modifier.clickable { onClick() } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        // ===== CORNICE (FRAME) & EFFETTI =====
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (cosmetics.frame) {
                FrameType.SCI_FI -> {
                    SciFiFrameContent(
                        pulseScale = pulseScale,
                        rotationAngle = rotationAngle,
                        particleProgress = particleProgress
                    )
                }
                FrameType.NONE -> {
                    // Nessuna cornice
                }
                else -> {
                    StandardFrameContent(frameType = cosmetics.frame)
                }
            }
        }

        // ===== AVATAR BASE =====
        AvatarBaseContent(
            innerSize = innerSize,
            size = size,
            cosmetics = cosmetics
        )
    }
}

@Composable
private fun SciFiFrameContent(
    pulseScale: Float,
    rotationAngle: Float,
    particleProgress: Float
) {
    // 1. BAGLIORE AURA NEON SULLO SFONDO (Aura luminosa)
    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = this.size.width / 2f
        val centerY = this.size.height / 2f
        val baseRadius = (this.size.minDimension / 2f) * pulseScale

        // Anello di luce soffusa esterna
        drawCircle(
            color = AvatarFixedColors.NeonGreenGlow.copy(alpha = 0.18f),
            radius = baseRadius * 1.12f,
            center = Offset(centerX, centerY)
        )
        drawCircle(
            color = AvatarFixedColors.NeonGreenGlow.copy(alpha = 0.35f),
            radius = baseRadius * 0.98f,
            center = Offset(centerX, centerY)
        )
    }

    // 2. SVG DELLA CORNICE SCI-FI ANIMATA
    Icon(
        painter = painterResource(id = R.drawable.ic_frame_scifi),
        contentDescription = "Cornice Sci-Fi Animata",
        tint = Color.Unspecified,
        modifier = Modifier
            .fillMaxSize()
            .scale(pulseScale)
            .rotate(rotationAngle)
    )

    // 3. PARTICELLE NEON IN PRIMO PIANO (Disegnate SOPRA la cornice)
    SciFiNeonParticles(
        modifier = Modifier.fillMaxSize(),
        progress = particleProgress
    )
}

@Composable
private fun StandardFrameContent(frameType: FrameType) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
            .clip(CircleShape)
            .border(
                width = 4.dp,
                color = frameType.color,
                shape = CircleShape
            )
            .background(frameType.color.copy(alpha = 0.15f))
    )
}

@Composable
private fun AvatarBaseContent(
    innerSize: androidx.compose.ui.unit.Dp,
    size: Int,
    cosmetics: AvatarCosmetics
) {
    Box(
        modifier = Modifier
            .size(innerSize)
            .clip(CircleShape)
            .background(AvatarFixedColors.AvatarSurface)
            .border(1.dp, AvatarFixedColors.AvatarBorder, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_avatar_default),
            contentDescription = "Avatar",
            tint = AvatarFixedColors.AvatarIconTint,
            modifier = Modifier
                .fillMaxSize()
                .size((size * 0.55).dp)
        )

        // COSMETICO TESTA (HAT)
        if (cosmetics.hat != HatType.NONE) {
            val hatSymbol = when (cosmetics.hat) {
                HatType.MAGO -> "🧙‍♂️"
                HatType.ELMO_CAVALIERE -> "🪖"
                HatType.VISOR_FUTURISTICO -> "🥽"
                HatType.NONE -> ""
            }

            Text(
                text = hatSymbol,
                fontSize = (size * 0.18).sp,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }

        // COSMETICO ARMA (WEAPON)
        if (cosmetics.weapon != WeaponType.NONE) {
            val weaponSymbol = when (cosmetics.weapon) {
                WeaponType.STAFF -> "🪄"
                WeaponType.SWORD -> "⚔️"
                WeaponType.GUN -> "🔫"
                WeaponType.NONE -> ""
            }

            Text(
                text = weaponSymbol,
                fontSize = (size * 0.16).sp,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}

/**
 * Canvas dedicato per le particelle neon con effetto bagliore radiale (Glow).
 */
@Composable
private fun SciFiNeonParticles(
    modifier: Modifier = Modifier,
    progress: Float,
    particleCount: Int = 22
) {
    // Genera la configurazione delle particelle
    val particles = remember {
        List(particleCount) {
            ParticleData(
                baseAngle = Random.nextFloat() * 2f * Math.PI.toFloat(),
                // Ampiezza raggio: da 0.42 (vicino al bordo) fino a 0.58 (fuori dalla cornice)
                radiusFactor = Random.nextFloat() * 0.18f + 0.42f,
                size = Random.nextFloat() * 5f + 4f, // Dimensione particella (4dp - 9dp)
                alphaOffset = Random.nextFloat() * 2f * Math.PI.toFloat(),
                speedFactor = Random.nextFloat() * 0.8f + 0.4f,
                isPixel = Random.nextBoolean()
            )
        }
    }

    Canvas(modifier = modifier) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val maxRadius = size.width / 2f

        particles.forEach { particle ->
            // Calcolo dell'angolo e dell'orbita
            val currentAngle = particle.baseAngle + (progress * 2f * Math.PI.toFloat() * particle.speedFactor)
            val currentRadius = maxRadius * (particle.radiusFactor + (sin(progress * 2f * Math.PI.toFloat() * 1.5f + particle.alphaOffset) * 0.04f))

            // Posizione (X, Y)
            val px = centerX + currentRadius * cos(currentAngle)
            val py = centerY + currentRadius * sin(currentAngle)

            // Opacità pulsante (effetto lucciola / lampo neon)
            val alpha = ((sin(progress * 2f * Math.PI.toFloat() * 2f + particle.alphaOffset) + 1f) / 2f) * 0.7f + 0.3f

            val glowColor = AvatarFixedColors.NeonGreenGlow.copy(alpha = alpha * 0.45f)
            val coreColor = AvatarFixedColors.NeonGreenCore.copy(alpha = alpha)

            if (particle.isPixel) {
                // QUADRATINO ARCADE NEON (Con doppio strato per l'effetto Glow)
                // 1. Alone sfumato esterno
                drawRect(
                    color = glowColor,
                    topLeft = Offset(px - particle.size, py - particle.size),
                    size = Size(particle.size * 2f, particle.size * 2f)
                )
                // 2. Cuore luminoso centrale
                drawRect(
                    color = coreColor,
                    topLeft = Offset(px - particle.size / 2f, py - particle.size / 2f),
                    size = Size(particle.size, particle.size)
                )
            } else {
                // CERCHIO NEON GLOW
                // 1. Alone sfumato esterno
                drawCircle(
                    color = glowColor,
                    radius = particle.size * 1.4f,
                    center = Offset(px, py)
                )
                // 2. Cuore luminoso centrale
                drawCircle(
                    color = coreColor,
                    radius = particle.size / 2f,
                    center = Offset(px, py)
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