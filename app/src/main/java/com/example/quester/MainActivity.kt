package com.example.quester

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.quester.data.database.DatabaseProvider
import com.example.quester.data.repository.AuthRepository
import com.example.quester.data.repository.MissionRepository
import com.example.quester.data.repository.UserRepository
import com.example.quester.data.session.SessionManager
import com.example.quester.domain.service.AuthService
import com.example.quester.domain.service.CurrencyService
import com.example.quester.domain.service.MissionService
import com.example.quester.domain.service.ShopService
import com.example.quester.data.model.ShopItem
import androidx.compose.runtime.LaunchedEffect
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
            val currencyService = remember { CurrencyService(userRepository) }
            val missionService = remember { MissionService(missionRepository, userRepository, currencyService) }
            val shopService = remember { ShopService(userRepository, database.shopDao(), database.ownedCosmeticDao()) }

            // Pre-populate shop items for demo
            LaunchedEffect(Unit) {
                database.shopDao().upsertItems(
                    listOf(
                        ShopItem("skin_knight", "Armatura da Cavaliere", 500),
                        ShopItem("skin_wizard", "Veste da Mago", 800),
                        ShopItem("skin_rogue", "Mantello del Ladro", 400),
                        ShopItem("skin_dragon", "Squame di Drago", 2000),
                        ShopItem("skin_slime", "Pelle di Slime", 100)
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
                        shopDao = database.shopDao()
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

/*@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    QuesterTheme {
        NavBar()
    }
}*/