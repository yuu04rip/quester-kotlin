package com.example.quester.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.example.quester.ui.components.ArcadeBackground
import com.example.quester.ui.components.AvatarCosmetics
import com.example.quester.ui.components.RoyalBackground // 👑 Importato il background regale
import com.example.quester.ui.screens.customization.AvatarCustomizationScreen
import com.example.quester.ui.screens.mission.MissionListScreen
import com.example.quester.ui.theme.AppTheme
import com.example.quester.ui.theme.ThemeManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class NavServices(
    val missionService: MissionService,
    val authService: AuthService,
    val shopService: ShopService
)

data class NavRepositories(
    val missionRepository: MissionRepository,
    val userRepository: UserRepository,
    val shopDao: ShopDao
)

@Composable
fun NavBar(
    services: NavServices,
    repositories: NavRepositories,
    sessionManager: SessionManager
) {
    val screens = listOf(
        NavScreens.Profile,
        NavScreens.Home,
        NavScreens.Shop
    )

    val pagerState = rememberPagerState(initialPage = 1) { screens.size }
    val coroutineScope = rememberCoroutineScope()
    var showCustomization by remember { mutableStateOf(false) }

    val currentUserId by sessionManager.loggedUserId.collectAsState(initial = null)

    // Leggiamo il tema corrente per decidere quale sfondo mostrare
    val currentTheme by ThemeManager.currentTheme.collectAsState()

    // Contatore per forzare la ri-lettura dal DB dopo ogni salvataggio
    var refreshTrigger by remember { mutableIntStateOf(0) }
    val equippedCosmetics by rememberEquippedCosmeticsState(repositories.userRepository, currentUserId, refreshTrigger)
    val ownedItemIds by rememberOwnedCosmeticsState(repositories.userRepository, currentUserId)

    // Gestione dinamica dello sfondo in base al tema attivo
    val contentWithBackground: @Composable (@Composable () -> Unit) -> Unit = { content ->
        when (currentTheme) {
            AppTheme.ARCADE -> {
                ArcadeBackground(modifier = Modifier.fillMaxSize()) { content() }
            }
            AppTheme.REGALE -> {
                RoyalBackground(modifier = Modifier.fillMaxSize()) { content() }
            }
            else -> {
                // Per i temi Default o Fantasy standard usiamo il colore di sfondo del MaterialTheme
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    content()
                }
            }
        }
    }

    contentWithBackground {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                if (!showCustomization) {
                    NavBarBottomNavigation(
                        screens = screens,
                        currentPage = pagerState.currentPage,
                        onPageSelect = { index ->
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                    )
                }
            },
            contentColor = MaterialTheme.colorScheme.onBackground
        ) { innerPadding ->
            if (showCustomization) {
                AvatarCustomizationScreen(
                    initialCosmetics = equippedCosmetics,
                    ownedItemIds = ownedItemIds,
                    onBack = { showCustomization = false },
                    onSave = { updatedCosmetics ->
                        saveAvatarChanges(
                            coroutineScope = coroutineScope,
                            userRepository = repositories.userRepository,
                            userId = currentUserId,
                            cosmetics = updatedCosmetics,
                            onComplete = {
                                refreshTrigger++ // Incrementa il trigger per forzare l'aggiornamento
                                showCustomization = false
                            }
                        )
                    }
                )
            } else {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .background(Color.Transparent)
                ) { pageIndex ->
                    NavBarPagerContent(
                        screen = screens[pageIndex],
                        services = services,
                        repositories = repositories,
                        sessionManager = sessionManager,
                        coroutineScope = coroutineScope,
                        onShowCustomization = { showCustomization = true }
                    )
                }
            }
        }
    }
}

@Composable
private fun rememberEquippedCosmeticsState(
    userRepository: UserRepository,
    userId: Long?,
    refreshTrigger: Int
): State<AvatarCosmetics> {
    return produceState(initialValue = AvatarCosmetics(), key1 = userId, key2 = refreshTrigger) {
        if (userId != null) {
            value = userRepository.getEquippedCosmetics(userId)
        }
    }
}

@Composable
private fun rememberOwnedCosmeticsState(
    userRepository: UserRepository,
    userId: Long?
): State<Set<String>> {
    val ownedFlow = remember(userId) {
        userId?.let { userRepository.getOwnedCosmeticsFlow(it) }
    }
    val ownedList by (ownedFlow?.collectAsState(initial = emptyList())
        ?: remember { mutableStateOf(emptyList()) })

    return remember(ownedList) {
        derivedStateOf { ownedList.map { it.itemId }.toSet() }
    }
}

private fun saveAvatarChanges(
    coroutineScope: CoroutineScope,
    userRepository: UserRepository,
    userId: Long?,
    cosmetics: AvatarCosmetics,
    onComplete: () -> Unit
) {
    coroutineScope.launch {
        if (userId != null) {
            userRepository.saveEquippedCosmetics(userId, cosmetics)
        }
        onComplete()
    }
}

@Composable
private fun NavBarBottomNavigation(
    screens: List<NavScreens>,
    currentPage: Int,
    onPageSelect: (Int) -> Unit
) {
    NavigationBar(
        containerColor = Color(0xA90D0B14),
        tonalElevation = 0.dp,
        modifier = Modifier.background(Color(0xA90D0B14))
    ) {
        screens.forEachIndexed { index, screen ->
            val isSelected = currentPage == index
            val contentColor = if (isSelected) {
                MaterialTheme.colorScheme.secondary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            }

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.title,
                        tint = contentColor
                    )
                },
                label = {
                    Text(
                        text = screen.title,
                        color = contentColor
                    )
                },
                selected = isSelected,
                onClick = { onPageSelect(index) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.secondary,
                    selectedTextColor = MaterialTheme.colorScheme.secondary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    indicatorColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f)
                )
            )
        }
    }
}

@Composable
private fun NavBarPagerContent(
    screen: NavScreens,
    services: NavServices,
    repositories: NavRepositories,
    sessionManager: SessionManager,
    coroutineScope: CoroutineScope,
    onShowCustomization: () -> Unit
) {
    when (screen) {
        is NavScreens.Profile -> ProfileScreen(
            userRepository = repositories.userRepository,
            sessionManager = sessionManager,
            onLogout = {
                coroutineScope.launch { services.authService.logout() }
            },
            onDeleteAccount = {
                coroutineScope.launch { services.authService.deleteAccount() }
            },
            onUpdateUsername = { newUsername ->
                coroutineScope.launch { services.authService.updateUsername(newUsername) }
            },
            onShowCustomization = onShowCustomization
        )
        is NavScreens.Home -> MissionListScreen(
            missionService = services.missionService,
            missionRepository = repositories.missionRepository,
            userRepository = repositories.userRepository,
            sessionManager = sessionManager
        )
        is NavScreens.Shop -> ShopScreen(
            shopService = services.shopService,
            shopDao = repositories.shopDao,
            userRepository = repositories.userRepository,
            sessionManager = sessionManager
        )
    }
}