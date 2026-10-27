package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DailyRegion
import com.example.model.Screen
import com.example.model.UserSession
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.Navy500
import com.example.ui.theme.Navy900

data class GridTabItem(
    val screen: Screen,
    val title: String,
    val subtitle: String,
    val badge: String,
    val icon: ImageVector,
    val accentColor: Color,
    val testTag: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    user: UserSession?,
    isDarkMode: Boolean,
    unreadNotificationCount: Int,
    totalPaid: Double,
    totalDue: Double,
    todayRegion: DailyRegion,
    onToggleDarkMode: () -> Unit,
    onOpenNotifications: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToTab: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    // 6 Tabs as drawn in the handwritten notebook image:
    // Row 1: Outstanding | Payments
    // Row 2: Orders | Price list
    // Row 3: Special Price Deals | Task Tracker
    val tabs = listOf(
        GridTabItem(
            screen = Screen.OUTSTANDING,
            title = "Outstanding",
            subtitle = "Ledger & dues",
            badge = "₹${totalDue.toLong()}",
            icon = Icons.Default.AccountBalanceWallet,
            accentColor = CrimsonRed,
            testTag = "tab_outstanding"
        ),
        GridTabItem(
            screen = Screen.PAYMENTS,
            title = "Payments",
            subtitle = "Collections & receipts",
            badge = "₹${totalPaid.toLong()}",
            icon = Icons.Default.ReceiptLong,
            accentColor = EmeraldGreen,
            testTag = "tab_payments"
        ),
        GridTabItem(
            screen = Screen.ORDERS,
            title = "Orders",
            subtitle = "Pipeline & shipments",
            badge = "4 Active",
            icon = Icons.Default.ShoppingCart,
            accentColor = ElectricCyan,
            testTag = "tab_orders"
        ),
        GridTabItem(
            screen = Screen.PRICE_LIST,
            title = "Price List",
            subtitle = "Wholesale rate catalog",
            badge = "Catalog",
            icon = Icons.Default.Description,
            accentColor = AmberGold,
            testTag = "tab_price_list"
        ),
        GridTabItem(
            screen = Screen.SPECIAL_DEALS,
            title = "Special Price Deals",
            subtitle = "Party & volume offers",
            badge = "4 Active",
            icon = Icons.Default.LocalOffer,
            accentColor = Color(0xFF8B5CF6),
            testTag = "tab_special_price_deals"
        ),
        GridTabItem(
            screen = Screen.TASK_TRACKER,
            title = "Task Tracker",
            subtitle = "Follow-ups & dispatch",
            badge = "3 Pending",
            icon = Icons.Default.Assignment,
            accentColor = Color(0xFF0EA5E9),
            testTag = "tab_task_tracker"
        )
    )

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_scaffold"),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Mahalaxmi Enterprise",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.2.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Welcome, ${user?.name ?: "Partner"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    // Push Notifications Bell
                    IconButton(
                        onClick = onOpenNotifications,
                        modifier = Modifier.testTag("notifications_bell_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadNotificationCount > 0) {
                                    Badge(
                                        containerColor = CrimsonRed,
                                        contentColor = Color.White
                                    ) {
                                        Text(text = "$unreadNotificationCount")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Push Notifications"
                            )
                        }
                    }

                    // Dark Mode Toggle
                    IconButton(
                        onClick = onToggleDarkMode,
                        modifier = Modifier.testTag("dark_mode_toggle_home")
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = if (isDarkMode) "Light Mode" else "Dark Mode",
                            tint = if (isDarkMode) AmberGold else MaterialTheme.colorScheme.primary
                        )
                    }

                    // Logout
                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier.testTag("logout_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Logout"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("home_grid_tabs"),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header card: today's region visit target and collections against it
            item(span = { GridItemSpan(2) }) {
                DailyTargetCard(
                    region = todayRegion,
                    onViewPayments = { onNavigateToTab(Screen.PAYMENTS) }
                )
            }

            // Section Header
            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Business Modules",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "6 Portals",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 6 Grid Tabs
            items(tabs.size) { index ->
                val tab = tabs[index]
                HomeGridCard(
                    tab = tab,
                    onClick = { onNavigateToTab(tab.screen) }
                )
            }

            // Bottom informational banner
            item(span = { GridItemSpan(2) }) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = EmeraldGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Real-time updates active. Tap any tab to view or manage details.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DailyTargetCard(
    region: DailyRegion,
    onViewPayments: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalStores = region.stores.size
    val visitedStores = region.stores.count { it.isVisited }
    val visitPercent = if (totalStores > 0) visitedStores * 100 / totalStores else 0

    val collectionDue = region.stores.sumOf { it.collectionDue }
    val collected = region.stores.sumOf { it.collectedToday }
    val collectionFraction = if (collectionDue > 0) (collected / collectionDue).coerceIn(0.0, 1.0) else 0.0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("daily_target_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Daily Target Completed",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = region.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ElectricCyan.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "$visitPercent% Done",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = ElectricCyan,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$visitedStores / $totalStores",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "stores visited",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Collections done against today's region dues
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onViewPayments() }
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Collections Done",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "₹${collected.toLong()} / ₹${collectionDue.toLong()}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = EmeraldGreen
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { collectionFraction.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = EmeraldGreen,
                    trackColor = EmeraldGreen.copy(alpha = 0.18f)
                )
            }
        }
    }
}

@Composable
fun HomeGridCard(
    tab: GridTabItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .testTag(tab.testTag),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Icon & Badge Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(tab.accentColor.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.title,
                        tint = tab.accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = tab.accentColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = tab.badge,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = tab.accentColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        maxLines = 1
                    )
                }
            }

            // Titles
            Column {
                Text(
                    text = tab.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        lineHeight = 20.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = tab.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
