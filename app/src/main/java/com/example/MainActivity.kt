package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.Screen
import com.example.notification.NotificationHelper
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NotificationSheet
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.OutstandingScreen
import com.example.ui.screens.PaymentsScreen
import com.example.ui.screens.PriceListScreen
import com.example.ui.screens.SpecialDealsScreen
import com.example.ui.screens.TaskTrackerScreen
import com.example.ui.theme.MahalaxmiTheme
import com.example.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Android notification channel for alerts
        NotificationHelper.createNotificationChannel(this)

        setContent {
            val mainViewModel: MainViewModel = viewModel()
            val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()
            val context = LocalContext.current

            // Request Notification Permission on Android 13+ (API 33+)
            val notificationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { /* permission granted/denied handled gracefully */ }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }

            MahalaxmiTheme(darkTheme = uiState.isDarkMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    var paymentPrefillParty by remember { mutableStateOf("") }
                    var paymentPrefillAmount by remember { mutableStateOf(0.0) }

                    when (uiState.currentScreen) {
                        Screen.LOGIN -> {
                            AuthScreen(
                                isDarkMode = uiState.isDarkMode,
                                onToggleDarkMode = { mainViewModel.toggleDarkMode() },
                                onLoginSuccess = { email, name ->
                                    mainViewModel.login(email, name)
                                }
                            )
                        }

                        Screen.HOME -> {
                            val unreadNotifs = uiState.notificationsList.count { !it.isRead }
                            val totalPaid = uiState.paymentsList.sumOf { it.amount }
                            val totalDue = uiState.outstandingList.sumOf { it.dueAmount }

                            HomeScreen(
                                user = uiState.currentUser,
                                isDarkMode = uiState.isDarkMode,
                                unreadNotificationCount = unreadNotifs,
                                totalPaid = totalPaid,
                                totalDue = totalDue,
                                todayRegion = uiState.todayRegion,
                                onToggleDarkMode = { mainViewModel.toggleDarkMode() },
                                onOpenNotifications = { mainViewModel.toggleNotificationSheet(true) },
                                onLogout = { mainViewModel.logout() },
                                onNavigateToTab = { tabScreen ->
                                    mainViewModel.navigateTo(tabScreen)
                                }
                            )
                        }

                        Screen.OUTSTANDING -> {
                            OutstandingScreen(
                                outstandingList = uiState.outstandingList,
                                onBack = { mainViewModel.navigateBack() },
                                onRecordPaymentForParty = { partyName, dueAmount ->
                                    paymentPrefillParty = partyName
                                    paymentPrefillAmount = dueAmount
                                    mainViewModel.navigateTo(Screen.PAYMENTS)
                                }
                            )
                        }

                        Screen.PAYMENTS -> {
                            PaymentsScreen(
                                paymentsList = uiState.paymentsList,
                                onBack = {
                                    paymentPrefillParty = ""
                                    paymentPrefillAmount = 0.0
                                    mainViewModel.navigateBack()
                                },
                                onRecordNewPayment = { party, amount, mode, ref ->
                                    mainViewModel.recordPayment(context, party, amount, mode, ref)
                                },
                                initialPartyName = paymentPrefillParty,
                                initialAmount = paymentPrefillAmount
                            )
                        }

                        Screen.ORDERS -> {
                            OrdersScreen(
                                ordersList = uiState.ordersList,
                                onBack = { mainViewModel.navigateBack() },
                                onCreateOrder = { party, summary, amount ->
                                    mainViewModel.createOrder(context, party, summary, amount)
                                }
                            )
                        }

                        Screen.PRICE_LIST -> {
                            PriceListScreen(
                                priceList = uiState.priceList,
                                onBack = { mainViewModel.navigateBack() }
                            )
                        }

                        Screen.SPECIAL_DEALS -> {
                            SpecialDealsScreen(
                                dealsList = uiState.specialDealsList,
                                onBack = { mainViewModel.navigateBack() }
                            )
                        }

                        Screen.TASK_TRACKER -> {
                            TaskTrackerScreen(
                                tasksList = uiState.tasksList,
                                onBack = { mainViewModel.navigateBack() },
                                onToggleTask = { taskId -> mainViewModel.toggleTask(taskId) },
                                onAddTask = { title, assignee, priority, tag ->
                                    mainViewModel.addTask(context, title, assignee, priority, tag)
                                }
                            )
                        }
                    }

                    // Push Notification Center Bottom Sheet
                    if (uiState.showNotificationSheet) {
                        NotificationSheet(
                            notifications = uiState.notificationsList,
                            onDismiss = { mainViewModel.toggleNotificationSheet(false) },
                            onMarkAllRead = { mainViewModel.markNotificationsAsRead() },
                            onSimulatePushNotification = { title, msg, cat ->
                                mainViewModel.triggerPushNotification(context, title, msg, cat)
                            }
                        )
                    }
                }
            }
        }
    }
}
