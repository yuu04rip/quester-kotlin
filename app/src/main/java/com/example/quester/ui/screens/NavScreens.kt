package com.example.quester.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavScreens(val route: String, val title: String, val icon: ImageVector) {
    object Profile : NavScreens("profile", "Profilo", Icons.Default.Person)
    object Home : NavScreens("home", "Home", Icons.Default.Home)
    object Shop : NavScreens("shop", "Negozio", Icons.Default.ShoppingCart)
}