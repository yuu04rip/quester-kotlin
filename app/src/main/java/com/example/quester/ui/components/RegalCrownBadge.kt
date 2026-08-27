package com.example.quester.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.quester.R
import com.example.quester.ui.theme.FantasyGoldLight

@Composable
fun RegalCrownBadge(
    modifier: Modifier = Modifier,
    size: Dp = 46.dp,
    isUnlocked: Boolean
) {
    // Se non è sbloccata, non mostra nulla
    if (!isUnlocked) return

    val totalSize = size + 16.dp

    Box(
        modifier = modifier
            .size(totalSize)
            .shadow(
                elevation = 12.dp,
                shape = CircleShape,
                ambientColor = FantasyGoldLight,
                spotColor = FantasyGoldLight
            ),
        contentAlignment = Alignment.Center
    ) {
        // Immagine della corona (assicurati di avere regal_crown.png in res/drawable)
        Image(
            painter = painterResource(id = R.drawable.regal_crown),
            contentDescription = "Corona Regale",
            modifier = Modifier.size(size),
            contentScale = ContentScale.Fit
        )
    }
}