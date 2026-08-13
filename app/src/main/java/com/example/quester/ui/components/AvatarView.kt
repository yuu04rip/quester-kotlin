package com.example.quester.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.quester.R
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random


// ============================================================
// TIPI DI COSMETICI
// ============================================================

enum class HatType(
    val displayName: String,
    val iconResource: Int? = null
) {
    NONE("Nessuno", null),
    MAGO("Cappello del Mago", null),
    ELMO_CAVALIERE("Elmo del Cavaliere", null),
    VISOR_FUTURISTICO("Visore Futuristico", null)
}

enum class WeaponType(
    val displayName: String,
    val iconResource: Int? = null
) {
    NONE("Nessuna", null),
    STAFF("Bastone del Mago", null),
    SWORD("Spada del Cavaliere", null),
    GUN("Pistola Spaziale", null)
}


// ============================================================
// CORNICI
// ============================================================

enum class FrameType(
    val displayName: String,
    val color: Color,
    val iconResource: Int? = null
) {
    NONE(
        "Nessuna",
        Color.Transparent,
        null
    ),

    BASIC(
        "Cornice Base",
        Color(0xFFD4AF37),
        R.drawable.frame_basic
    ),

    MAGO(
        "Cornice del Mago",
        Color(0xFF6B4C9A),
        null // Oppure R.drawable.frame_mago se hai l'SVG
    ),

    CAVALIERE(
        "Cornice del Cavaliere",
        Color(0xFFD4AF37),
        null // Oppure R.drawable.frame_cavaliere se hai l'SVG
    ),

    SCI_FI(
        "Cornice Sci-Fi",
        Color(0xFF00FF66),
        null
    )
}

// ============================================================
// COSMETICI AVATAR
// ============================================================

data class AvatarCosmetics(
    val hat: HatType = HatType.NONE,
    val weapon: WeaponType = WeaponType.NONE,
    val frame: FrameType = FrameType.NONE
)


// ============================================================
// COLORI FISSI AVATAR
// ============================================================

private object AvatarFixedColors {
    val ContainerBackground = Color(0xFF1E1B2E)
    val AvatarSurface = Color(0xFF2B283A)
    val AvatarBorder = Color(0xFF44405A)
    val AvatarIconTint = Color(0xFFA5A1B8)
    val NeonGreenGlow = Color(0xFF00FF66)
    val NeonGreenCore = Color(0xFFE0FFEC)
}


// ============================================================
// DATI PARTICELLA
// ============================================================

private data class ParticleData(
    val baseAngle: Float,
    val radiusFactor: Float,
    val size: Float,
    val alphaOffset: Float,
    val speedFactor: Float,
    val isPixel: Boolean
)


// ============================================================
// AVATAR VIEW
// ============================================================

@Composable
fun AvatarView(
    modifier: Modifier = Modifier,
    cosmetics: AvatarCosmetics = AvatarCosmetics(),
    size: Dp = 200.dp,
    scale: Float = 1.0f,
    verticalOffset: Dp = 0.dp, // <--- Nuovo parametro per spostare in alto/basso
    onClick: () -> Unit = {},
    isEditable: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ArcadeFrameTransition")

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ArcadeRotation"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ArcadePulse"
    )

    val particleProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ParticleProgress"
    )

    Box(
        modifier = modifier
            .size(size)
            .then(
                if (isEditable) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        // Cornice
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
                FrameType.NONE -> {}
                else -> {
                    // Se la cornice ha un'icona risorsa associata (come BASIC), la disegniamo
                    if (cosmetics.frame.iconResource != null) {
                        Image(
                            painter = painterResource(id = cosmetics.frame.iconResource!!),
                            contentDescription = cosmetics.frame.displayName,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        StandardFrameContent(frameType = cosmetics.frame)
                    }
                }
            }
        }

        // Corpo Avatar con supporto a scale e spostamento verticale
        AvatarBaseContent(
            innerSize = size,
            cosmetics = cosmetics,
            scale = scale,
            verticalOffset = verticalOffset
        )
    }
}


// ============================================================
// CORNICE SCI-FI
// ============================================================

@Composable
private fun SciFiFrameContent(
    pulseScale: Float,
    rotationAngle: Float,
    particleProgress: Float
) {
    val frameScale = 0.85f * pulseScale
    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val baseRadius = (size.minDimension / 2f) * frameScale

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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .scale(frameScale), // <--- Usa la scala ridotta con l'animazione pulse inclusa
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_frame_scifi),
         contentDescription = "Cornice Sci-Fi Animata",
            tint = Color.Unspecified,
            modifier = Modifier
                .fillMaxSize()
            .rotate(rotationAngle)
        )
    }

    SciFiNeonParticles(
        modifier = Modifier.fillMaxSize(),
        progress = particleProgress
    )
}


// ============================================================
// CORNICE STANDARD
// ============================================================

@Composable
private fun StandardFrameContent(frameType: FrameType) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
            .clip(CircleShape)
            .border(width = 4.dp, color = frameType.color, shape = CircleShape)
            .background(frameType.color.copy(alpha = 0.15f))
    )
}


// ============================================================
// CORPO AVATAR ( LAYER INDIPENDENTI CON ZOOM E OFFSET )
// ============================================================

@Composable
private fun AvatarBaseContent(
    innerSize: Dp,
    cosmetics: AvatarCosmetics,
    scale: Float,
    verticalOffset: Dp
) {
    Box(
        modifier = Modifier
            .size(innerSize)
            .scale(scale)
            .offset(y = verticalOffset),
        contentAlignment = Alignment.Center
    ) {
        // LAYER 1 - ARMA (Usa quella di default se è NONE, altrimenti la pistola laser)
        val weaponRes = when (cosmetics.weapon) {
            WeaponType.GUN -> R.drawable.char_weapon_laser
            else -> R.drawable.char_weapon_wood // Default (per NONE, SWORD, STAFF)
        }

        Image(
            painter = painterResource(id = weaponRes),
            contentDescription = "Arma",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        // LAYER 2 - CORPO BASE (Sempre presente)
        Image(
            painter = painterResource(id = R.drawable.char_body),
            contentDescription = "Corpo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        // LAYER 3 - OUTFIT (Vestito di default)
        Image(
            painter = painterResource(id = R.drawable.char_outfit),
            contentDescription = "Vestito",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        // LAYER 4 - CAPPELLO (Usa quello di default se è NONE, altrimenti il visore)
        val hatRes = when (cosmetics.hat) {
            HatType.VISOR_FUTURISTICO -> R.drawable.char_visor
            else -> R.drawable.char_hat // Default (per NONE, MAGO, ELMO_CAVALIERE)
        }

        Image(
            painter = painterResource(id = hatRes),
            contentDescription = "Copricapo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
    }
}

// ============================================================
// PARTICELLE NEON SCI-FI
// ============================================================

@Composable
private fun SciFiNeonParticles(
    modifier: Modifier = Modifier,
    progress: Float,
    particleCount: Int = 22
) {
    val particles = remember {
        List(particleCount) {
            ParticleData(
                baseAngle = Random.nextFloat() * 2f * Math.PI.toFloat(),
                radiusFactor = Random.nextFloat() * 0.18f + 0.42f,
                size = Random.nextFloat() * 5f + 4f,
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
            val currentAngle = particle.baseAngle + (progress * 2f * Math.PI.toFloat() * particle.speedFactor)
            val currentRadius = maxRadius * (particle.radiusFactor + (sin(progress * 2f * Math.PI.toFloat() * 1.5f + particle.alphaOffset) * 0.04f))

            val px = centerX + currentRadius * cos(currentAngle)
            val py = centerY + currentRadius * sin(currentAngle)

            val alpha = ((sin(progress * 2f * Math.PI.toFloat() * 2f + particle.alphaOffset) + 1f) / 2f) * 0.7f + 0.3f
            val glowColor = AvatarFixedColors.NeonGreenGlow.copy(alpha = alpha * 0.45f)
            val coreColor = AvatarFixedColors.NeonGreenCore.copy(alpha = alpha)

            if (particle.isPixel) {
                drawRect(
                    color = glowColor,
                    topLeft = Offset(px - particle.size, py - particle.size),
                    size = Size(particle.size * 2f, particle.size * 2f)
                )
                drawRect(
                    color = coreColor,
                    topLeft = Offset(px - particle.size / 2f, py - particle.size / 2f),
                    size = Size(particle.size, particle.size)
                )
            } else {
                drawCircle(
                    color = glowColor,
                    radius = particle.size * 1.4f,
                    center = Offset(px, py)
                )
                drawCircle(
                    color = coreColor,
                    radius = particle.size / 2f,
                    center = Offset(px, py)
                )
            }
        }
    }
}


// ============================================================
// PREVIEW AVATAR
// ============================================================

@Composable
fun AvatarPreview(
    cosmetics: AvatarCosmetics = AvatarCosmetics(),
    size: Dp = 200.dp,
    scale: Float = 1.0f,
    verticalOffset: Dp = 0.dp,
    modifier: Modifier = Modifier
) {
    AvatarView(
        modifier = modifier,
        cosmetics = cosmetics,
        size = size,
        scale = scale,
        verticalOffset = verticalOffset,
        isEditable = false
    )
}