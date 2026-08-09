package com.example.quester.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quester.data.model.OwnedCosmetic

@Composable
fun OwnedCosmeticsSection(
    ownedCosmetics: List<OwnedCosmetic>,
    modifier: Modifier = Modifier
) {

    val cosmeticSize = 104.dp
    val spacing = 10.dp

    if (ownedCosmetics.isEmpty()) {
        return
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {

        ownedCosmetics
            .chunked(3)
            .forEach { rowItems ->

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        spacing
                    )
                ) {

                    rowItems.forEach { cosmetic ->

                        FantasyCosmeticItem(
                            itemId = cosmetic.itemId,
                            modifier = Modifier.size(
                                cosmeticSize
                            )
                        )
                    }

                    repeat(3 - rowItems.size) {

                        Spacer(
                            modifier = Modifier.size(
                                cosmeticSize
                            )
                        )
                    }
                }
            }
    }
}

@Composable
fun FantasyCosmeticItem(
    itemId: String,
    modifier: Modifier = Modifier
) {

    val shape = RoundedCornerShape(18.dp)

    Box(
        modifier = modifier
            .shadow(
                elevation = 10.dp,
                shape = shape
            )
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        FantasySurfaceLight,
                        FantasySurface
                    )
                )
            )
            .border(
                width = 1.5.dp,
                color = FantasyGold.copy(
                    alpha = 0.55f
                ),
                shape = shape
            )
    ) {

        Box(
            modifier = Modifier
                .size(62.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.radialGradient(
                        listOf(
                            FantasyPurple.copy(
                                alpha = 0.35f
                            ),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            FantasyPurple,
                            FantasyGold,
                            FantasyPurple
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 8.dp,
                    vertical = 10.dp
                ),

            horizontalAlignment = Alignment.CenterHorizontally,

            verticalArrangement = Arrangement.Center
        ) {

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        FantasyPurpleDark.copy(
                            alpha = 0.85f
                        )
                    )
                    .border(
                        1.dp,
                        FantasyGold.copy(
                            alpha = 0.45f
                        ),
                        CircleShape
                    ),

                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = itemId
                        .take(1)
                        .uppercase(),

                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = FantasyGoldLight,
                    fontFamily = FontFamily.Serif
                )
            }

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = itemId
                    .take(8)
                    .uppercase(),

                style = MaterialTheme.typography.labelMedium,

                fontWeight = FontWeight.Bold,

                color = FantasyText,

                fontFamily = FontFamily.Monospace,

                maxLines = 1
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Box(
                modifier = Modifier
                    .size(
                        width = 24.dp,
                        height = 2.dp
                    )
                    .clip(
                        RoundedCornerShape(2.dp)
                    )
                    .background(
                        FantasyGold.copy(
                            alpha = 0.65f
                        )
                    )
            )
        }
    }
}