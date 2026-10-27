package com.example.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SampleEnterpriseData
import com.example.model.DailyRegion
import com.example.model.OrderRecord
import com.example.model.OutstandingRecord
import com.example.model.PaymentRecord
import com.example.model.PriceItem
import com.example.model.PushNotification
import com.example.model.Screen
import com.example.model.SpecialDeal
import com.example.model.TaskRecord
import com.example.model.UserSession
import com.example.notification.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

data class MainUiState(
    val currentUser: UserSession? = UserSession(
        email = "krutidadriwal1834@gmail.com",
        name = "Krutika D.",
        enterpriseName = "Mahalaxmi Enterprise",
        role = "Managing Partner"
    ),
    val isDarkMode: Boolean = false,
    val currentScreen: Screen = Screen.HOME,
    val outstandingList: List<OutstandingRecord> = SampleEnterpriseData.initialOutstanding,
    val paymentsList: List<PaymentRecord> = SampleEnterpriseData.initialPayments,
    val ordersList: List<OrderRecord> = SampleEnterpriseData.initialOrders,
    val priceList: List<PriceItem> = SampleEnterpriseData.initialPriceList,
    val specialDealsList: List<SpecialDeal> = SampleEnterpriseData.initialSpecialDeals,
    val tasksList: List<TaskRecord> = SampleEnterpriseData.initialTasks,
    val notificationsList: List<PushNotification> = SampleEnterpriseData.initialNotifications,
    val todayRegion: DailyRegion = SampleEnterpriseData.todayRegion,
    val showNotificationSheet: Boolean = false
)

class MainViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun toggleDarkMode() {
        _uiState.update { it.copy(isDarkMode = !it.isDarkMode) }
    }

    fun setDarkMode(enabled: Boolean) {
        _uiState.update { it.copy(isDarkMode = enabled) }
    }

    fun login(email: String, name: String) {
        val user = UserSession(
            email = email.ifBlank { "partner@mahalaxmienterprise.com" },
            name = name.ifBlank { "Partner" },
            enterpriseName = "Mahalaxmi Enterprise",
            role = "Managing Partner"
        )
        _uiState.update {
            it.copy(
                currentUser = user,
                currentScreen = Screen.HOME
            )
        }
    }

    fun logout() {
        _uiState.update {
            it.copy(
                currentUser = null,
                currentScreen = Screen.LOGIN
            )
        }
    }

    fun navigateTo(screen: Screen) {
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun navigateBack() {
        _uiState.update { it.copy(currentScreen = Screen.HOME) }
    }

    fun toggleNotificationSheet(show: Boolean) {
        _uiState.update { it.copy(showNotificationSheet = show) }
    }

    fun markNotificationsAsRead() {
        _uiState.update { state ->
            state.copy(
                notificationsList = state.notificationsList.map { it.copy(isRead = true) }
            )
        }
    }

    fun triggerPushNotification(
        context: Context,
        title: String,
        message: String,
        category: String = "Enterprise"
    ) {
        val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        val notifId = Random.nextInt(1000, 99999)

        val newNotification = PushNotification(
            id = "notif-$notifId",
            title = title,
            message = message,
            timestamp = "Just now ($timeStr)",
            category = category,
            isRead = false
        )

        _uiState.update { state ->
            state.copy(
                notificationsList = listOf(newNotification) + state.notificationsList
            )
        }

        NotificationHelper.showPushNotification(
            context = context,
            notificationId = notifId,
            title = title,
            message = message,
            category = category
        )
    }

    fun recordPayment(
        context: Context,
        partyName: String,
        amount: Double,
        mode: String,
        refNo: String
    ) {
        val timeStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date())
        val newPayment = PaymentRecord(
            id = "pay-${System.currentTimeMillis()}",
            partyName = partyName,
            amount = amount,
            date = timeStr,
            mode = mode,
            referenceNo = refNo.ifBlank { "UPI/${Random.nextLong(10000000000L, 99999999999L)}" }
        )

        _uiState.update { state ->
            val updatedOutstanding = state.outstandingList.map { item ->
                if (item.partyName.equals(partyName, ignoreCase = true)) {
                    val newPaid = item.paidAmount + amount
                    val newDue = (item.totalBilled - newPaid).coerceAtLeast(0.0)
                    item.copy(paidAmount = newPaid, dueAmount = newDue, lastPaymentDate = "Today")
                } else {
                    item
                }
            }
            state.copy(
                paymentsList = listOf(newPayment) + state.paymentsList,
                outstandingList = updatedOutstanding
            )
        }

        triggerPushNotification(
            context = context,
            title = "Payment Received: ₹${amount.toLong()}",
            message = "Received ₹${amount.toLong()} from $partyName via $mode",
            category = "PAYMENTS"
        )
    }

    fun createOrder(
        context: Context,
        partyName: String,
        summary: String,
        amount: Double
    ) {
        val ordNum = "ORD-2026-${Random.nextInt(1051, 9999)}"
        val timeStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        val newOrder = OrderRecord(
            id = "ord-${System.currentTimeMillis()}",
            orderNumber = ordNum,
            partyName = partyName,
            date = timeStr,
            itemsSummary = summary,
            totalAmount = amount,
            status = "Processing"
        )

        _uiState.update { state ->
            state.copy(ordersList = listOf(newOrder) + state.ordersList)
        }

        triggerPushNotification(
            context = context,
            title = "New Order $ordNum",
            message = "Order for ₹${amount.toLong()} placed for $partyName",
            category = "ORDERS"
        )
    }

    fun toggleTask(taskId: String) {
        _uiState.update { state ->
            state.copy(
                tasksList = state.tasksList.map {
                    if (it.id == taskId) it.copy(isCompleted = !it.isCompleted) else it
                }
            )
        }
    }

    fun addTask(
        context: Context,
        title: String,
        assignee: String,
        priority: String,
        tag: String
    ) {
        val newTask = TaskRecord(
            id = "task-${System.currentTimeMillis()}",
            title = title,
            assignee = assignee.ifBlank { "Team" },
            dueDate = "Today, 05:00 PM",
            priority = priority,
            isCompleted = false,
            tag = tag
        )

        _uiState.update { state ->
            state.copy(tasksList = listOf(newTask) + state.tasksList)
        }

        triggerPushNotification(
            context = context,
            title = "New Task Assigned",
            message = "$title assigned to ${newTask.assignee}",
            category = "TASK"
        )
    }
}
