package com.example.quester.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.quester.ui.screens.mission.MissionListScreen
import kotlinx.coroutines.launch

// Colori Tema Fantasy (stessi degli altri schermi)
private val FantasyBackground = Color(0xFF0D0B14)
private val FantasySurface = Color(0xFF171321)
private val FantasyGoldLight = Color(0xFFF0CC78)
private val FantasyPurple = Color(0xFF6B4C9A)
private val FantasyText = Color(0xFFF3EBD8)
private val FantasyTextSecondary = Color(0xFFC8BDA8)

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
    val screens = listOf(
        NavScreens.Profile,
        NavScreens.Home,
        NavScreens.Shop
    )

    val pagerState = rememberPagerState(initialPage = 1) { screens.size }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        containerColor = FantasyBackground,
        bottomBar = {
            NavigationBar(
                containerColor = FantasySurface,
                tonalElevation = 8.dp,
                modifier = Modifier.background(FantasySurface)
            ) {
                screens.forEachIndexed { index, screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                screen.icon,
                                contentDescription = screen.title,
                                tint = if (pagerState.currentPage == index) {
                                    FantasyGoldLight
                                } else {
                                    FantasyTextSecondary
                                }
                            )
                        },
                        label = {
                            Text(
                                screen.title,
                                color = if (pagerState.currentPage == index) {
                                    FantasyGoldLight
                                } else {
                                    FantasyTextSecondary
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
                            selectedIconColor = FantasyGoldLight,
                            selectedTextColor = FantasyGoldLight,
                            unselectedIconColor = FantasyTextSecondary,
                            unselectedTextColor = FantasyTextSecondary,
                            indicatorColor = FantasyPurple.copy(alpha = 0.3f)
                        )
                    )
                }
            }
        },
        contentColor = FantasyText
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .padding(innerPadding)
                .background(FantasyBackground)
        ) { pageIndex ->
            when (screens[pageIndex]) {
                is NavScreens.Profile -> ProfileScreen(
                    userRepository = userRepository,
                    sessionManager = sessionManager,
                    onLogout = {
                        coroutineScope.launch {
                            authService.logout()
                        }
                    }
                )
                is NavScreens.Home -> MissionListScreen(
                    missionService = missionService,
                    missionRepository = missionRepository,
                    userRepository = userRepository,
                    sessionManager = sessionManager
                )
                is NavScreens.Shop -> ShopScreen(
                    shopService = shopService,
                    shopDao = shopDao,
                    userRepository = userRepository,
                    sessionManager = sessionManager
                )
            }
        }
    }
}