package com.example.data

import com.example.model.DailyRegion
import com.example.model.OrderRecord
import com.example.model.OutstandingRecord
import com.example.model.PaymentRecord
import com.example.model.PriceItem
import com.example.model.PushNotification
import com.example.model.RegionStore
import com.example.model.SpecialDeal
import com.example.model.TaskRecord

object SampleEnterpriseData {

    val todayRegion = DailyRegion(
        name = "Surat - Ring Road",
        stores = listOf(
            RegionStore("st-1", "Shree Balaji Textiles", "Ring Road", isVisited = true, collectionDue = 45000.0, collectedToday = 20000.0),
            RegionStore("st-2", "Laxmi Saree Centre", "Ring Road", isVisited = true, collectionDue = 18000.0, collectedToday = 18000.0),
            RegionStore("st-3", "Om Fabrics", "Salabatpura", isVisited = true, collectionDue = 22000.0),
            RegionStore("st-4", "Krishna Dress Material", "Salabatpura", collectionDue = 15000.0),
            RegionStore("st-5", "Jay Ambe Textiles", "Udhna Darwaja", collectionDue = 30000.0),
            RegionStore("st-6", "Mahavir Cloth Store", "Udhna Darwaja", collectionDue = 12000.0),
            RegionStore("st-7", "Sai Kripa Garments", "Sahara Darwaja", collectionDue = 8000.0),
            RegionStore("st-8", "New Rajhans Silk Mills", "Sahara Darwaja", collectionDue = 25000.0)
        )
    )

    val initialOutstanding = listOf(
        OutstandingRecord(
            id = "out-1",
            partyName = "Shree Balaji Textiles",
            city = "Surat",
            totalBilled = 120000.0,
            paidAmount = 75000.0,
            dueAmount = 45000.0,
            overdueDays = 34,
            lastPaymentDate = "18 Oct 2026",
            phone = "+91 98251 44102"
        ),
        OutstandingRecord(
            id = "out-2",
            partyName = "Radhe Shyam Fab Hub",
            city = "Ahmedabad",
            totalBilled = 240000.0,
            paidAmount = 160000.0,
            dueAmount = 80000.0,
            overdueDays = 45,
            lastPaymentDate = "10 Oct 2026",
            phone = "+91 98790 33211"
        ),
        OutstandingRecord(
            id = "out-3",
            partyName = "Gupta & Sons Traders",
            city = "Mumbai",
            totalBilled = 85000.0,
            paidAmount = 55000.0,
            dueAmount = 30000.0,
            overdueDays = 14,
            lastPaymentDate = "22 Oct 2026",
            phone = "+91 98200 99411"
        ),
        OutstandingRecord(
            id = "out-4",
            partyName = "Kothari Garment Works",
            city = "Indore",
            totalBilled = 150000.0,
            paidAmount = 90000.0,
            dueAmount = 60000.0,
            overdueDays = 28,
            lastPaymentDate = "15 Oct 2026",
            phone = "+91 94250 88200"
        ),
        OutstandingRecord(
            id = "out-5",
            partyName = "Vardhman Cloth Agency",
            city = "Bhilwara",
            totalBilled = 95000.0,
            paidAmount = 65000.0,
            dueAmount = 30000.0,
            overdueDays = 8,
            lastPaymentDate = "24 Oct 2026",
            phone = "+91 98290 11984"
        )
    )

    val initialPayments = listOf(
        PaymentRecord(
            id = "pay-1",
            partyName = "Vardhman Cloth Agency",
            amount = 35000.0,
            date = "Today, 11:30 AM",
            mode = "UPI / PhonePe",
            referenceNo = "UPI/26102711304"
        ),
        PaymentRecord(
            id = "pay-2",
            partyName = "Gupta & Sons Traders",
            amount = 25000.0,
            date = "Yesterday",
            mode = "NEFT / HDFC",
            referenceNo = "HDFC00291941"
        ),
        PaymentRecord(
            id = "pay-3",
            partyName = "Shree Balaji Textiles",
            amount = 50000.0,
            date = "24 Oct 2026",
            mode = "Cheque Clearing",
            referenceNo = "CHQ-884012"
        ),
        PaymentRecord(
            id = "pay-4",
            partyName = "Radhe Shyam Fab Hub",
            amount = 75000.0,
            date = "21 Oct 2026",
            mode = "RTGS / SBI",
            referenceNo = "SBIN09228172"
        )
    )

    val initialOrders = listOf(
        OrderRecord(
            id = "ord-1",
            orderNumber = "ORD-2026-1048",
            partyName = "Shree Balaji Textiles",
            date = "26 Oct 2026",
            itemsSummary = "Premium Cotton Grey (1,200m), Poly Twill (800m)",
            totalAmount = 92400.0,
            status = "Processing"
        ),
        OrderRecord(
            id = "ord-2",
            orderNumber = "ORD-2026-1049",
            partyName = "Radhe Shyam Fab Hub",
            date = "25 Oct 2026",
            itemsSummary = "Loom Shirting Quality A (600m)",
            totalAmount = 48000.0,
            status = "Dispatched"
        ),
        OrderRecord(
            id = "ord-3",
            orderNumber = "ORD-2026-1050",
            partyName = "Kothari Garment Works",
            date = "Today, 09:15 AM",
            itemsSummary = "Heavy Rayon 140GSM (2,000m)",
            totalAmount = 138000.0,
            status = "Pending"
        ),
        OrderRecord(
            id = "ord-4",
            orderNumber = "ORD-2026-1046",
            partyName = "Gupta & Sons Traders",
            date = "22 Oct 2026",
            itemsSummary = "Silk Weave Dupattas (350 pcs)",
            totalAmount = 64750.0,
            status = "Delivered"
        )
    )

    val initialPriceList = listOf(
        PriceItem(
            id = "p-1",
            code = "MLX-COT-101",
            name = "Pure Cotton Grey 60x60",
            category = "Cotton Fabrics",
            unit = "Meter",
            wholesaleRate = 42.50,
            mrp = 65.00
        ),
        PriceItem(
            id = "p-2",
            code = "MLX-POL-204",
            name = "Poly Twill Heavy Duty 58\"",
            category = "Polyester",
            unit = "Meter",
            wholesaleRate = 51.00,
            mrp = 78.00
        ),
        PriceItem(
            id = "p-3",
            code = "MLX-RAY-305",
            name = "Rayon 140GSM Dyed Finish",
            category = "Rayon",
            unit = "Meter",
            wholesaleRate = 69.00,
            mrp = 95.00
        ),
        PriceItem(
            id = "p-4",
            code = "MLX-SHR-401",
            name = "Loom Shirting Quality A (Check)",
            category = "Shirting",
            unit = "Meter",
            wholesaleRate = 80.00,
            mrp = 120.00
        ),
        PriceItem(
            id = "p-5",
            code = "MLX-SLK-502",
            name = "Banarasi Silk Weave Border Dupatta",
            category = "Ethnic Weaves",
            unit = "Piece",
            wholesaleRate = 185.00,
            mrp = 320.00
        ),
        PriceItem(
            id = "p-6",
            code = "MLX-LIN-608",
            name = "Linen Blend Chambray 60\"",
            category = "Linen",
            unit = "Meter",
            wholesaleRate = 110.00,
            mrp = 165.00
        )
    )

    val initialSpecialDeals = listOf(
        SpecialDeal(
            id = "deal-1",
            title = "Festive Season Bulk Booking",
            partyOrSegment = "All Verified Wholesalers",
            discountPercent = 8.5,
            specialRate = null,
            validUntil = "05 Nov 2026",
            condition = "Minimum billing quantity 2,500 meters across categories"
        ),
        SpecialDeal(
            id = "deal-2",
            title = "Radhe Shyam Volume Special Rate",
            partyOrSegment = "Radhe Shyam Fab Hub",
            discountPercent = 12.0,
            specialRate = 38.00,
            validUntil = "15 Nov 2026",
            condition = "Special contract rate for Pure Cotton Grey 60x60"
        ),
        SpecialDeal(
            id = "deal-3",
            title = "End-of-Month Rayon Stock Clearance",
            partyOrSegment = "Ready Stock Buyers",
            discountPercent = 15.0,
            specialRate = 58.50,
            validUntil = "31 Oct 2026",
            condition = "Full bale dispatch (minimum 500m per bale)"
        ),
        SpecialDeal(
            id = "deal-4",
            title = "Prompt Payment Incentive (Within 7 Days)",
            partyOrSegment = "All Tier-1 Accounts",
            discountPercent = 2.5,
            specialRate = null,
            validUntil = "31 Dec 2026",
            condition = "Direct NEFT/RTGS settlement within 7 days of invoice date"
        )
    )

    val initialTasks = listOf(
        TaskRecord(
            id = "task-1",
            title = "Follow-up with Shree Balaji for pending balance ₹45,000",
            assignee = "Krutika (Accounts)",
            dueDate = "Today, 04:00 PM",
            priority = "High",
            isCompleted = false,
            tag = "Payment Follow-up"
        ),
        TaskRecord(
            id = "task-2",
            title = "Confirm transport dispatch LR for ORD-1049 to Ahmedabad",
            assignee = "Ramesh (Logistics)",
            dueDate = "Today, 06:30 PM",
            priority = "High",
            isCompleted = false,
            tag = "Dispatch"
        ),
        TaskRecord(
            id = "task-3",
            title = "Verify sample yardage for Linen Blend Chambray inquiry",
            assignee = "Dev (Sales)",
            dueDate = "Tomorrow, 11:00 AM",
            priority = "Medium",
            isCompleted = false,
            tag = "Price Quote"
        ),
        TaskRecord(
            id = "task-4",
            title = "Review revised wholesale price list with Managing Partner",
            assignee = "Admin",
            dueDate = "29 Oct 2026",
            priority = "Normal",
            isCompleted = true,
            tag = "Accounts"
        )
    )

    val initialNotifications = listOf(
        PushNotification(
            id = "notif-1",
            title = "Payment Received: ₹35,000",
            message = "Vardhman Cloth Agency paid ₹35,000 via UPI. Dues updated.",
            timestamp = "11:30 AM",
            category = "PAYMENTS",
            isRead = false
        ),
        PushNotification(
            id = "notif-2",
            title = "New Order #ORD-1050 Received",
            message = "Kothari Garment Works booked 2,000m Heavy Rayon for ₹1,38,000.",
            timestamp = "09:15 AM",
            category = "ORDERS",
            isRead = false
        ),
        PushNotification(
            id = "notif-3",
            title = "Overdue Alert: Radhe Shyam Fab Hub",
            message = "Balance ₹80,000 is overdue by 45 days. Action recommended.",
            timestamp = "Yesterday",
            category = "OUTSTANDING",
            isRead = true
        )
    )
}
