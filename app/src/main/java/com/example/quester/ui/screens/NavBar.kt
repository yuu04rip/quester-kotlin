package com.example.quester.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.quester.data.dao.ShopDao
import com.example.quester.data.repository.MissionRepository
import com.example.quester.data.repository.UserRepository
import com.example.quester.data.session.SessionManager
import com.example.quester.domain.service.AuthService
import com.example.quester.domain.service.MissionService
import com.example.quester.domain.service.ShopService
import com.example.quester.ui.screens.customization.AvatarCustomizationScreen
import com.example.quester.ui.screens.mission.MissionListScreen
import kotlinx.coroutines.launch

// ===== DATA CLASS PER I PARAMETRI =====

private data class NavServices(
    val missionService: MissionService,
    val missionRepository: MissionRepository,
    val userRepository: UserRepository,
    val authService: AuthService,
    val shopService: ShopService,
    val shopDao: ShopDao,
    val sessionManager: SessionManager
)

private data class NavState(
    val showCustomization: Boolean,
    val pagerState: androidx.compose.foundation.pager.PagerState,
    val screens: List<NavScreens>,
    val innerPadding: PaddingValues
)

private data class NavCallbacks(
    val onCustomizationDismiss: () -> Unit,
    val onCustomizationSave: () -> Unit,
    val onShowCustomization: () -> Unit
)

// ===== NAVBAR PRINCIPALE =====

@Composable
fun NavBar(
    missionService: MissionService,
    missionRepository: MissionRepository,
    userRepository: UserRepository,
    authService: AuthService,
    shopService: ShopService,
    shopDao: ShopDao,
    sessionManager: SessionManager
) {
    val services = NavServices(
        missionService = missionService,
        missionRepository = missionRepository,
        userRepository = userRepository,
        authService = authService,
        shopService = shopService,
        shopDao = shopDao,
        sessionManager = sessionManager
    )

    val screens = listOf(
        NavScreens.Profile,
        NavScreens.Home,
        NavScreens.Shop
    )

    val pagerState = rememberPagerState(initialPage = 1) { screens.size }
    val coroutineScope = rememberCoroutineScope()
    var showCustomization by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                screens.forEachIndexed { index, screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title,
                                tint = if (pagerState.currentPage == index) {
                                    MaterialTheme.colorScheme.secondary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                color = if (pagerState.currentPage == index) {
                                    MaterialTheme.colorScheme.secondary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.secondary,
                            selectedTextColor = MaterialTheme.colorScheme.secondary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        )
                    )
                }
            }
        },
        contentColor = MaterialTheme.colorScheme.onBackground
    ) { innerPadding ->
        val state = NavState(
            showCustomization = showCustomization,
            pagerState = pagerState,
            screens = screens,
            innerPadding = innerPadding
        )

        val callbacks = NavCallbacks(
            onCustomizationDismiss = { showCustomization = false },
            onCustomizationSave = { showCustomization = false },
            onShowCustomization = { showCustomization = true }
        )

        NavContent(
            services = services,
            state = state,
            callbacks = callbacks,
            coroutineScope = coroutineScope
        )
    }
}

// ===== CONTENUTO NAV =====

@Composable
private fun NavContent(
    services: NavServices,
    state: NavState,
    callbacks: NavCallbacks,
    coroutineScope: kotlinx.coroutines.CoroutineScope
) {
    if (state.showCustomization) {
        AvatarCustomizationScreen(
            onBack = callbacks.onCustomizationDismiss,
            onSave = { _ ->
                // TODO: Salva i cosmetici nel repository
                callbacks.onCustomizationSave()
            }
        )
    } else {
        HorizontalPager(
            state = state.pagerState,
            modifier = Modifier
                .padding(state.innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) { pageIndex ->
            when (state.screens[pageIndex]) {
                is NavScreens.Profile -> ProfileContent(
                    services = services,
                    coroutineScope = coroutineScope,
                    onShowCustomization = callbacks.onShowCustomization
                )
                is NavScreens.Home -> MissionListScreen(
                    missionService = services.missionService,
                    missionRepository = services.missionRepository,
                    userRepository = services.userRepository,
                    sessionManager = services.sessionManager
                )
                is NavScreens.Shop -> ShopScreen(
                    shopService = services.shopService,
                    shopDao = services.shopDao,
                    userRepository = services.userRepository,
                    sessionManager = services.sessionManager
                )
            }
        }
    }
}

// ===== CONTENUTO PROFILO =====

@Composable
private fun ProfileContent(
    services: NavServices,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    onShowCustomization: () -> Unit
) {
    ProfileScreen(
        userRepository = services.userRepository,
        sessionManager = services.sessionManager,
        onLogout = {
            coroutineScope.launch {
                services.authService.logout()
            }
        },
        onDeleteAccount = {
            coroutineScope.launch {
                services.authService.deleteAccount()
            }
        },
        onUpdateUsername = { newUsername ->
            coroutineScope.launch {
                services.authService.updateUsername(newUsername)
            }
        },
        onShowCustomization = onShowCustomization
    )
}