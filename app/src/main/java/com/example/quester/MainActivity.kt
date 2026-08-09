package com.example.quester

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.quester.data.database.DatabaseProvider
import com.example.quester.data.model.ShopItem
import com.example.quester.data.repository.AuthRepository
import com.example.quester.data.repository.MissionRepository
import com.example.quester.data.repository.UserRepository
import com.example.quester.data.session.SessionManager
import com.example.quester.domain.service.*
import com.example.quester.ui.screens.AuthScreen
import com.example.quester.ui.screens.NavBar
import com.example.quester.ui.theme.QuesterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = this
            val database = remember { DatabaseProvider.getDatabase(context) }
            val sessionManager = remember { SessionManager(context) }
            val authRepository = remember { AuthRepository(database.userDao()) }
            val authService = remember { AuthService(authRepository, sessionManager) }

            val userRepository = remember { UserRepository(database.userDao(), database.ownedCosmeticDao()) }
            val missionRepository = remember { MissionRepository(database.missionDao(), database.subTaskDao()) }

            val currencyService = remember { CurrencyService(userRepository, sessionManager) }

            val securityNotificationService = remember { SecurityNotificationService(context) }

            val missionService = remember {
                MissionService(
                    missionRepository = missionRepository,
                    userRepository = userRepository,
                    currencyService = currencyService,
                    sessionManager = sessionManager,
                    securityNotificationService = securityNotificationService
                )
            }

            val shopService = remember {
                ShopService(
                    userRepository = userRepository,
                    shopDao = database.shopDao(),
                    ownedDao = database.ownedCosmeticDao(),
                    sessionManager = sessionManager
                )
            }

            // Pre-populate shop items - SENZA id
            LaunchedEffect(Unit) {
                database.shopDao().upsertItems(
                    listOf(
                        ShopItem(itemId = "skin_slime", name = "Pelle di Slime", price = 100),
                        ShopItem(itemId = "skin_rogue", name = "Mantello del Ladro", price = 400),
                        ShopItem(itemId = "skin_knight", name = "Armatura da Cavaliere", price = 500),
                        ShopItem(itemId = "skin_wizard", name = "Veste da Mago", price = 800),
                        ShopItem(itemId = "skin_dragon", name = "Squame di Drago", price = 2000)
                    )
                )
            }

            val isAuthenticated by authService.isAuthenticated.collectAsState(initial = false)

            QuesterTheme {
                if (isAuthenticated) {
                    NavBar(
                        missionService = missionService,
                        missionRepository = missionRepository,
                        userRepository = userRepository,
                        authService = authService,
                        shopService = shopService,
                        shopDao = database.shopDao(),
                        sessionManager = sessionManager
                    )
                } else {
                    AuthScreen(
                        authService = authService,
                        onAuthSuccess = { /* Flow updates automatically */ }
                    )
                }
            }
        }
    }
}