package com.example.quester

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.quester.data.database.DatabaseProvider
import com.example.quester.data.model.ShopItem
import com.example.quester.data.preferences.ThemePreferences
import com.example.quester.data.repository.AuthRepository
import com.example.quester.data.repository.MissionRepository
import com.example.quester.data.repository.UserRepository
import com.example.quester.data.session.SessionManager
import com.example.quester.domain.service.*
import com.example.quester.ui.screens.AuthScreen
import com.example.quester.ui.screens.NavBar
import com.example.quester.ui.screens.NavRepositories
import com.example.quester.ui.screens.NavServices
import com.example.quester.ui.theme.QuesterTheme
import com.example.quester.ui.theme.ThemeManager
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            println("✅ Permesso notifiche concesso")
        } else {
            println("⚠️ Permesso notifiche negato")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        requestNotificationPermission()

        val themePreferences = ThemePreferences(this)

        lifecycleScope.launch {
            val savedTheme = themePreferences.getTheme()
            ThemeManager.setTheme(savedTheme)
        }

        setContent {
            val context = this
            val database = remember { DatabaseProvider.getDatabase(context) }
            val sessionManager = remember { SessionManager(context) }
            val authRepository = remember { AuthRepository(database.userDao()) }
            val userRepository = remember { UserRepository(database.userDao(), database.ownedCosmeticDao()) }
            val authService = remember { AuthService(authRepository, sessionManager, userRepository) }
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

            LaunchedEffect(Unit) {
                database.shopDao().deleteAllItems()
                database.shopDao().upsertItems(
                    listOf(
                        ShopItem(itemId = "frame_mago", name = "Cornice del Mago", price = 30, description = "Cornice con rune magiche e stelle cadenti", iconName = "shopping_cart"),
                        ShopItem(itemId = "frame_cavaliere", name = "Cornice del Cavaliere", price = 30, description = "Cornice con spade incrociate e scudi", iconName = "shopping_cart"),
                        ShopItem(itemId = "frame_scifi", name = "Cornice Sci-Fi", price = 30, description = "Cornice con circuiti luminosi e neon", iconName = "ic_frame_scifi"),
                        ShopItem(itemId = "hat_mago", name = "Cappello del Mago", price = 100, description = "Cappello a punta con stelle magiche", iconName = "shopping_cart"),
                        ShopItem(itemId = "staff_mago", name = "Bastone del Mago", price = 100, description = "Bastone con gemma magica incantata", iconName = "shopping_cart"),
                        ShopItem(itemId = "gun_spaziale", name = "Space Pistol", price = 100, description = "High-tech laser pistol", iconName = "ic_gun_spaziale"),
                        ShopItem(itemId = "sword_cavaliere", name = "Spada del Cavaliere", price = 100, description = "Spada luminosa forgiata nell'acciaio", iconName = "shopping_cart"),
                        ShopItem(itemId = "elmo_cavaliere", name = "Elmo del Cavaliere", price = 100, description = "Elmo con visiera protettiva", iconName = "shopping_cart"),
                        ShopItem(itemId = "visor_futuristico", name = "Visore Futuristico", price = 100, description = "Visore high-tech con HUD integrato", iconName = "ic_visor_futuristico"),
                        ShopItem(itemId = "theme_arcade", name = "Tema Arcade", price = 500, description = "Stile retrò con colori neon e pixel art", iconName = "ic_theme_arcade"),
                        ShopItem(itemId = "theme_fantasy", name = "Tema Bacheca Fantasy", price = 500, description = "Stile pergamena antica e rune magiche", iconName = "shopping_cart"),
                        ShopItem(itemId = "reward_corona", name = "👑 Corona dell'Eroe", price = 0, description = "★ Riservata ai veri Campioni! ★", iconName = "shopping_cart"),
                        ShopItem(itemId = "reward_tema_regale", name = "✦ Tema Regale", price = 0, description = "✦ Tema esclusivo per i Re di Quester", iconName = "shopping_cart")
                    )
                )
            }

            val isAuthenticated by authService.isAuthenticated.collectAsState(initial = false)
            val currentTheme by ThemeManager.currentTheme.collectAsState()

            QuesterTheme(
                darkTheme = true,
                themeType = currentTheme
            ) {
                if (isAuthenticated) {
                    NavBar(
                        services = NavServices(
                            missionService = missionService,
                            shopService = shopService,
                            authService = authService
                        ),
                        repositories = NavRepositories(
                            missionRepository = missionRepository,
                            userRepository = userRepository,
                            shopDao = database.shopDao()
                        ),
                        sessionManager = sessionManager
                    )
                } else {
                    AuthScreen(
                        authService = authService,
                        onAuthSuccess = {}
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
                ) == PackageManager.PERMISSION_GRANTED -> {}
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
}