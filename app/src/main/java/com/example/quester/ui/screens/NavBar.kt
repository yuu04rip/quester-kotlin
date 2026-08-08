package com.example.quester.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.quester.data.dao.ShopDao
import com.example.quester.data.repository.MissionRepository
import com.example.quester.data.repository.UserRepository
import com.example.quester.domain.service.AuthService
import com.example.quester.domain.service.MissionService
import com.example.quester.domain.service.ShopService
import com.example.quester.ui.screens.MissionList
import kotlinx.coroutines.launch

@Composable
fun NavBar(
    missionService: MissionService,
    missionRepository: MissionRepository,
    userRepository: UserRepository,
    authService: AuthService,
    shopService: ShopService,
    shopDao: ShopDao
) {
    // 1. Definiamo l'ordine visivo: Sinistra (Profilo) -> Centro (Home) -> Destra (Impostazioni)
    val screens = listOf(
        NavScreens.Profile,
        NavScreens.Home,
        NavScreens.Shop
    )

    // 2. initialPage = 1 dice al sistema di aprire l'app SULLA HOME (l'elemento al centro)
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
        // 3. HorizontalPager gestisce lo scorrimento
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.padding(innerPadding)
        ) { pageIndex ->

            // Colleghiamo in modo DINAMICO ogni oggetto NavScreens alla sua vera schermata
            when (screens[pageIndex]) {
                is NavScreens.Profile -> ProfileScreen(
                    userRepository = userRepository,
                    onLogout = {
                        coroutineScope.launch {
                            authService.logout()
                        }
                    }
                )
                is NavScreens.Home -> MissionList(
                    missionService = missionService,
                    missionRepository = missionRepository,
                    userRepository = userRepository
                )
                is NavScreens.Shop -> ShopScreen(
                    shopService = shopService,
                    shopDao = shopDao,
                    userRepository = userRepository
                )

            }
        }
    }
}

/*@Preview(showBackground = true)
@Composable
fun NavBarPreview() {
    NavBar()
}*/