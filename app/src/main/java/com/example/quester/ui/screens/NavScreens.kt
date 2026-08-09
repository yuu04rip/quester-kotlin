package com.example.quester.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavScreens(
    val title: String,
    val icon: ImageVector
) {
    data object Profile : NavScreens(
        title = "Profilo",
        icon = Icons.Default.Person
    )

    data object Home : NavScreens(
        title = "Missioni",
        icon = Icons.Default.Home
    )

    data object Shop : NavScreens(
        title = "Negozio",
        icon = Icons.Default.ShoppingBag
    )
}