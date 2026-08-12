package com.example.quester.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.platform.LocalContext
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

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

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
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

// ============================================================
// SHOP DATA
// ============================================================

@Composable
private fun rememberShopData(
    shopDao: ShopDao,
    userRepository: UserRepository,
    sessionManager: SessionManager
): ShopData {

    val loggedUserId by sessionManager.loggedUserId
        .collectAsState(initial = null)

    val user by (
            loggedUserId?.let {
                userRepository.getUserByIdFlow(it)
            } ?: flowOf(null)
            ).collectAsState(initial = null)

    val ownedItems by (
            loggedUserId?.let {
                userRepository.getOwnedCosmeticsFlow(it)
            } ?: flowOf(emptyList())
            ).collectAsState(initial = emptyList())

    val allShopItems by shopDao
        .getAllItemsFlow()
        .collectAsState(initial = emptyList())

    val ownedItemIds = ownedItems
        .map { it.itemId }
        .toSet()

    val sortedShopItems = remember(allShopItems, ownedItemIds) {
        getSortedShopItems(allShopItems, ownedItemIds)
    }

    return ShopData(
        userCoins = user?.coins ?: 0,
        ownedItems = ownedItems,
        allShopItems = allShopItems,
        ownedItemIds = ownedItemIds,
        sortedShopItems = sortedShopItems
    )
}

private fun getSortedShopItems(
    allShopItems: List<ShopItem>,
    ownedItemIds: Set<String>
): List<ShopItem> {
    return allShopItems.sortedWith(
        compareBy<ShopItem> {
            if (it.itemId in ownedItemIds) 1 else 0
        }.thenBy {
            it.price
        }
    )
}

// ============================================================
// CONTENT
// ============================================================

@Composable
private fun ShopContent(
    shopData: ShopData,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
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

// ============================================================
// HEADER
// ============================================================

@Composable
private fun ShopHeader(
    coins: Int,
    ownedCount: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                16.dp,
                RoundedCornerShape(20.dp)
            )
            .border(
                2.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.65f),
                RoundedCornerShape(20.dp)
            )
            .padding(2.dp)
            .border(
                1.dp,
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f),
                RoundedCornerShape(18.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(
                alpha = 0.98f
            )
        ),
        shape = RoundedCornerShape(18.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "✦ Negozio ✦",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )

            CoinsDisplay(coins)

            Text(
                text = "Oggetti posseduti: $ownedCount",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ============================================================
// COINS
// ============================================================

@Composable
private fun CoinsDisplay(coins: Int) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(R.drawable.coin),
            contentDescription = "Monete",
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = "$coins",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

// ============================================================
// GRID
// ============================================================

@Composable
private fun ShopGrid(
    items: List<ShopItem>,
    ownedItemIds: Set<String>,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    shopService: ShopService
) {

    if (items.isEmpty()) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "✦ Nessun oggetto disponibile ✦",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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

// ============================================================
// BUY
// ============================================================

private fun handleBuyItem(
    item: ShopItem,
    shopService: ShopService,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
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

// ============================================================
// ITEM CARD
// ============================================================

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
            .shadow(
                8.dp,
                RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = if (isOwned) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                } else {
                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                },
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isOwned) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
            }
        ),
        shape = RoundedCornerShape(16.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                4.dp,
                Alignment.CenterVertically
            )
        ) {

            ShopItemIcon(
                iconName = item.iconName,
                isOwned = isOwned
            )

            ShopItemName(
                name = item.name,
                isOwned = isOwned
            )

            if (isOwned) {

                OwnedBadge()

            } else {

                ShopItemPrice(item.price)

                BuyButton(onBuy)
            }
        }
    }
}

// ============================================================
// ITEM ICON (DINAMICO)
// ============================================================

@Composable
private fun ShopItemIcon(
    iconName: String,
    isOwned: Boolean
) {
    val context = LocalContext.current

    val drawableResId = remember(iconName) {
        if (iconName.isBlank() || iconName == "shopping_cart") {
            0
        } else {
            context.resources.getIdentifier(iconName, "drawable", context.packageName)
        }
    }

    Box(
        modifier = Modifier
            .size(48.dp)
            .background(
                color = if (isOwned) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                } else {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                },
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (drawableResId != 0) {
            Image(
                painter = painterResource(id = drawableResId),
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                colorFilter = if (isOwned) {
                    ColorFilter.colorMatrix(
                        ColorMatrix().apply { setToSaturation(0f) }
                    )
                } else {
                    null
                }
            )
        } else {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = null,
                tint = if (isOwned) {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                } else {
                    MaterialTheme.colorScheme.secondary
                },
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

// ============================================================
// NAME
// ============================================================

@Composable
private fun ShopItemName(
    name: String,
    isOwned: Boolean
) {

    Text(
        text = name,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = if (isOwned) {
            MaterialTheme.colorScheme.onSurfaceVariant
        } else {
            MaterialTheme.colorScheme.onBackground
        },
        textAlign = TextAlign.Center,
        maxLines = 2,
        minLines = 2,
        fontSize = 12.sp,
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
    )
}

// ============================================================
// OWNED
// ============================================================

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
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(14.dp)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = "Posseduto",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 10.sp
        )
    }
}

// ============================================================
// PRICE
// ============================================================

@Composable
private fun ShopItemPrice(price: Int) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(R.drawable.coin),
            contentDescription = "Monete",
            modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = "$price",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 14.sp
        )
    }
}

// ============================================================
// BUY BUTTON
// ============================================================

@Composable
private fun BuyButton(
    onBuy: () -> Unit
) {

    Spacer(modifier = Modifier.height(4.dp))

    Button(
        onClick = onBuy,
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        ),
        shape = RoundedCornerShape(6.dp),
        elevation = ButtonDefaults.buttonElevation(2.dp),
        contentPadding = PaddingValues(
            horizontal = 8.dp,
            vertical = 2.dp
        )
    ) {

        Text(
            text = "ACQUISTA",
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSecondary,
            letterSpacing = 0.5.sp
        )
    }
}