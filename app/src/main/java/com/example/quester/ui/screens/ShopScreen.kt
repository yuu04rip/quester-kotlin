package com.example.quester.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quester.R
import com.example.quester.data.dao.ShopDao
import com.example.quester.data.model.OwnedCosmetic
import com.example.quester.data.model.ShopItem
import com.example.quester.data.repository.UserRepository
import com.example.quester.data.session.SessionManager
import com.example.quester.domain.service.ShopService
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

// Colori Tema Fantasy
private val FantasyBackground = Color(0xFF0D0B14)
private val FantasySurface = Color(0xFF171321)
private val FantasySurfaceLight = Color(0xFF221B2E)
private val FantasyGold = Color(0xFFD4A84F)
private val FantasyGoldLight = Color(0xFFF0CC78)
private val FantasyPurple = Color(0xFF6B4C9A)
private val FantasyText = Color(0xFFF3EBD8)
private val FantasyTextSecondary = Color(0xFFC8BDA8)

// Data class per i dati dello shop
private data class ShopData(
    val userCoins: Int,
    val ownedItems: List<OwnedCosmetic>,
    val allShopItems: List<ShopItem>,
    val ownedItemIds: Set<String>,
    val sortedShopItems: List<ShopItem>
)

@Composable
fun ShopScreen(
    shopService: ShopService,
    shopDao: ShopDao,
    userRepository: UserRepository,
    sessionManager: SessionManager
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val shopData = rememberShopData(
        shopDao = shopDao,
        userRepository = userRepository,
        sessionManager = sessionManager
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF120C1E), FantasyBackground, Color(0xFF0B0813))
                )
            )
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            containerColor = Color.Transparent
        ) { paddingValues ->
            ShopContent(
                shopData = shopData,
                snackbarHostState = snackbarHostState,
                scope = scope,
                shopService = shopService,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            )
        }
    }
}

// ===== REMEMBER FUNCTIONS =====

@Composable
private fun rememberShopData(
    shopDao: ShopDao,
    userRepository: UserRepository,
    sessionManager: SessionManager
): ShopData {
    val loggedUserId by sessionManager.loggedUserId.collectAsState(initial = null)
    val user by (loggedUserId?.let { userRepository.getUserByIdFlow(it) } ?: flowOf(null))
        .collectAsState(initial = null)
    val ownedItems by (loggedUserId?.let { userRepository.getOwnedCosmeticsFlow(it) } ?: flowOf(emptyList()))
        .collectAsState(initial = emptyList())
    val allShopItems by shopDao.getAllItemsFlow().collectAsState(initial = emptyList())

    val ownedItemIds = ownedItems.map { it.itemId }.toSet()
    val sortedShopItems = allShopItems.sortedWith(
        compareBy<ShopItem> { if (it.itemId in ownedItemIds) 1 else 0 }
            .thenBy { it.price }
    )

    return ShopData(
        userCoins = user?.coins ?: 0,
        ownedItems = ownedItems,
        allShopItems = allShopItems,
        ownedItemIds = ownedItemIds,
        sortedShopItems = sortedShopItems
    )
}

// ===== SHOP CONTENT =====

@Composable
private fun ShopContent(
    shopData: ShopData,
    snackbarHostState: SnackbarHostState,
    scope: kotlinx.coroutines.CoroutineScope,
    shopService: ShopService,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        ShopHeader(
            coins = shopData.userCoins,
            ownedCount = shopData.ownedItems.size
        )

        Spacer(modifier = Modifier.height(16.dp))

        ShopGrid(
            items = shopData.sortedShopItems,
            ownedItemIds = shopData.ownedItemIds,
            snackbarHostState = snackbarHostState,
            scope = scope,
            shopService = shopService
        )
    }
}

// ===== HEADER =====

@Composable
private fun ShopHeader(
    coins: Int,
    ownedCount: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(20.dp))
            .border(2.dp, FantasyPurple.copy(alpha = 0.65f), RoundedCornerShape(20.dp))
            .padding(2.dp)
            .border(1.dp, FantasyGold.copy(alpha = 0.8f), RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = FantasySurface.copy(alpha = 0.98f)),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "✦ Negozio ✦",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = FantasyGoldLight
            )

            CoinsDisplay(coins = coins)

            Text(
                text = "Oggetti posseduti: $ownedCount",
                style = MaterialTheme.typography.bodySmall,
                color = FantasyTextSecondary
            )
        }
    }
}

// ===== COINS DISPLAY =====

@Composable
private fun CoinsDisplay(coins: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.coin),
            contentDescription = "Monete",
            modifier = Modifier.size(24.dp),
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$coins",
            style = MaterialTheme.typography.titleMedium,
            color = FantasyGold
        )
    }
}

// ===== SHOP GRID =====

@Composable
private fun ShopGrid(
    items: List<ShopItem>,
    ownedItemIds: Set<String>,
    snackbarHostState: SnackbarHostState,
    scope: kotlinx.coroutines.CoroutineScope,
    shopService: ShopService
) {
    if (items.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✦ Nessun oggetto disponibile ✦",
                color = FantasyTextSecondary,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { item ->
                val isOwned = item.itemId in ownedItemIds
                ShopItemCard(
                    item = item,
                    isOwned = isOwned,
                    onBuy = {
                        handleBuyItem(
                            item = item,
                            shopService = shopService,
                            snackbarHostState = snackbarHostState,
                            scope = scope
                        )
                    }
                )
            }
        }
    }
}

// ===== BUY HANDLER =====

private fun handleBuyItem(
    item: ShopItem,
    shopService: ShopService,
    snackbarHostState: SnackbarHostState,
    scope: kotlinx.coroutines.CoroutineScope
) {
    scope.launch {
        try {
            val success = shopService.buyItem(item.itemId)
            val message = if (success) {
                "✦ ${item.name} acquistato! ✦"
            } else {
                "✗ Monete insufficienti o già posseduto!"
            }
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        } catch (e: Exception) {
            snackbarHostState.showSnackbar(
                message = "Errore: ${e.message}",
                duration = SnackbarDuration.Short
            )
        }
    }
}

// ===== SHOP ITEM CARD =====

@Composable
private fun ShopItemCard(
    item: ShopItem,
    isOwned: Boolean,
    onBuy: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = if (isOwned) FantasyPurple.copy(alpha = 0.3f) else FantasyGold.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isOwned) {
                FantasySurfaceLight.copy(alpha = 0.5f)
            } else {
                FantasySurfaceLight.copy(alpha = 0.8f)
            }
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
        ) {
            ShopItemIcon(isOwned = isOwned)
            ShopItemName(name = item.name, isOwned = isOwned)

            if (isOwned) {
                OwnedBadge()
            } else {
                ShopItemPrice(price = item.price)
                BuyButton(onBuy = onBuy)
            }
        }
    }
}

// ===== SHOP ITEM SUB-COMPONENTS =====

@Composable
private fun ShopItemIcon(isOwned: Boolean) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(
                if (isOwned) FantasyPurple.copy(alpha = 0.2f) else FantasyPurple.copy(alpha = 0.3f),
                RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = null,
            tint = if (isOwned) FantasyTextSecondary else FantasyGold,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
private fun ShopItemName(name: String, isOwned: Boolean) {
    Text(
        text = name,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = if (isOwned) FantasyTextSecondary else FantasyText,
        textAlign = TextAlign.Center,
        maxLines = 2,
        minLines = 2,
        fontSize = 12.sp,
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
    )
}

@Composable
private fun OwnedBadge() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "Posseduto",
            tint = FantasyGold,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "Posseduto",
            style = MaterialTheme.typography.labelSmall,
            color = FantasyGold,
            fontSize = 10.sp
        )
    }
}

@Composable
private fun ShopItemPrice(price: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.coin),
            contentDescription = "Monete",
            modifier = Modifier.size(16.dp),
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$price",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = FantasyGold,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun BuyButton(onBuy: () -> Unit) {
    Spacer(modifier = Modifier.height(4.dp))
    Button(
        onClick = onBuy,
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = FantasyGold,
            contentColor = Color(0xFF0D0B14)
        ),
        shape = RoundedCornerShape(6.dp),
        elevation = ButtonDefaults.buttonElevation(2.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = "ACQUISTA",
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            color = Color(0xFF0D0B14),
            letterSpacing = 0.5.sp
        )
    }
}