package com.example.quester

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
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
import com.example.quester.utils.NotificationHelper

class MainActivity : ComponentActivity() {

    // Launcher per richiedere il permesso notifiche
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permesso concesso
            println("✅ Permesso notifiche concesso")
        } else {
            // Permesso negato - mostra un messaggio all'utente
            println("⚠️ Permesso notifiche negato")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Richiedi il permesso per le notifiche su Android 13+
        requestNotificationPermission()

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

            // Pre-populate shop items for demo
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

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permesso già concesso
                }
                else -> {
                    // Richiedi il permesso
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
}