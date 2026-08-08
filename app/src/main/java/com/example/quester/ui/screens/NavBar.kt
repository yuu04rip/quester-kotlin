package com.example.quester.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.quester.data.dao.ShopDao
import com.example.quester.data.repository.MissionRepository
import com.example.quester.data.repository.UserRepository
import com.example.quester.data.session.SessionManager
import com.example.quester.domain.service.AuthService
import com.example.quester.domain.service.MissionService
import com.example.quester.domain.service.ShopService
import com.example.quester.ui.screens.mission.MissionListScreen
import kotlinx.coroutines.launch

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
        bottomBar = {
            NavigationBar {
                screens.forEachIndexed { index, screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.padding(innerPadding)
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