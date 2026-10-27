package com.example.model

enum class Screen {
    LOGIN,
    HOME,
    OUTSTANDING,
    PAYMENTS,
    ORDERS,
    PRICE_LIST,
    SPECIAL_DEALS,
    TASK_TRACKER
}

enum class TabModule(
    val id: String,
    val title: String,
    val subtitle: String,
    val badge: String
) {
    OUTSTANDING(
        id = "outstanding",
        title = "Outstanding",
        subtitle = "Due ledger & aging",
        badge = "₹2,45,000"
    ),
    PAYMENTS(
        id = "payments",
        title = "Payments",
        subtitle = "Receipts & collections",
        badge = "14 Today"
    ),
    ORDERS(
        id = "orders",
        title = "Orders",
        subtitle = "Pipeline & shipments",
        badge = "8 Active"
    ),
    PRICE_LIST(
        id = "price_list",
        title = "Price List",
        subtitle = "Wholesale catalog rates",
        badge = "154 Items"
    ),
    SPECIAL_DEALS(
        id = "special_deals",
        title = "Special Price Deals",
        subtitle = "Volume rates & discounts",
        badge = "5 Live"
    ),
    TASK_TRACKER(
        id = "task_tracker",
        title = "Task Tracker",
        subtitle = "Follow-ups & dispatches",
        badge = "4 Pending"
    )
}

data class UserSession(
    val email: String,
    val name: String,
    val enterpriseName: String = "Mahalaxmi Enterprise",
    val role: String = "Enterprise Admin"
)

data class OutstandingRecord(
    val id: String,
    val partyName: String,
    val city: String,
    val totalBilled: Double,
    val paidAmount: Double,
    val dueAmount: Double,
    val overdueDays: Int,
    val lastPaymentDate: String,
    val phone: String
)

data class PaymentRecord(
    val id: String,
    val partyName: String,
    val amount: Double,
    val date: String,
    val mode: String, // UPI, NEFT, Cheque, Cash
    val referenceNo: String,
    val verified: Boolean = true
)

data class OrderRecord(
    val id: String,
    val orderNumber: String,
    val partyName: String,
    val date: String,
    val itemsSummary: String,
    val totalAmount: Double,
    val status: String // "Pending", "Processing", "Dispatched", "Delivered"
)

data class PriceItem(
    val id: String,
    val code: String,
    val name: String,
    val category: String,
    val unit: String,
    val wholesaleRate: Double,
    val mrp: Double,
    val inStock: Boolean = true
)

data class SpecialDeal(
    val id: String,
    val title: String,
    val partyOrSegment: String,
    val discountPercent: Double,
    val specialRate: Double?,
    val validUntil: String,
    val condition: String
)

data class TaskRecord(
    val id: String,
    val title: String,
    val assignee: String,
    val dueDate: String,
    val priority: String, // "High", "Medium", "Normal"
    val isCompleted: Boolean = false,
    val tag: String // "Payment Follow-up", "Dispatch", "Order Confirmation", "Price Quote"
)

// Region assigned to the DSO for the day; visiting every store in it is the daily target.
data class DailyRegion(
    val name: String,
    val stores: List<RegionStore>
)

data class RegionStore(
    val id: String,
    val storeName: String,
    val area: String,
    val isVisited: Boolean = false,
    val collectionDue: Double,
    val collectedToday: Double = 0.0
)

data class PushNotification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val category: String,
    val isRead: Boolean = false
)
