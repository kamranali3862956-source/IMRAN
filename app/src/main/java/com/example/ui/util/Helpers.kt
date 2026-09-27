package com.example.ui.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.WaterDamage
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.theme.PriorityEmergency
import com.example.ui.theme.PriorityHigh
import com.example.ui.theme.PriorityLow
import com.example.ui.theme.PriorityMedium
import com.example.ui.theme.StatusInProgress
import com.example.ui.theme.StatusInProgressContainer
import com.example.ui.theme.StatusPending
import com.example.ui.theme.StatusPendingContainer
import com.example.ui.theme.StatusRejected
import com.example.ui.theme.StatusRejectedContainer
import com.example.ui.theme.StatusResolved
import com.example.ui.theme.StatusResolvedContainer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AppUtils {

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatShortDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun getTimeAgo(timestamp: Long): String {
        val diff = System.currentTimeMillis() - timestamp
        val minutes = diff / (60 * 1000)
        val hours = diff / (60 * 60 * 1000)
        val days = diff / (24 * 60 * 60 * 1000)

        return when {
            minutes < 1 -> "Just now"
            minutes < 60 -> "$minutes min ago"
            hours < 24 -> "$hours hr ago"
            days == 1L -> "Yesterday"
            days < 7 -> "$days days ago"
            else -> formatDate(timestamp).split(",")[0]
        }
    }

    fun getCategoryIcon(category: String): ImageVector {
        return when (category.uppercase()) {
            "WATER" -> Icons.Default.LocalDrink
            "ELECTRICITY" -> Icons.Default.ElectricBolt
            "SANITATION" -> Icons.Default.WaterDamage
            "ROADS" -> Icons.Default.Build
            "HEALTH" -> Icons.Default.HealthAndSafety
            "EDUCATION" -> Icons.Default.School
            else -> Icons.Default.HelpOutline
        }
    }

    fun getCategoryNameUrdu(category: String): String {
        return when (category.uppercase()) {
            "WATER" -> "پانی کی فراہمی (Water)"
            "ELECTRICITY" -> "بجلی و لوڈشیڈنگ (Electricity)"
            "SANITATION" -> "صفائی و سیوریج (Sanitation)"
            "ROADS" -> "سڑکیں و گلیاں (Roads)"
            "HEALTH" -> "صحت و ڈسپنسری (Health)"
            "EDUCATION" -> "تعلیم و اسکول (Education)"
            else -> "دیگر مسائل (Other)"
        }
    }

    fun getStatusLabel(status: String): String {
        return when (status.uppercase()) {
            "PENDING" -> "موصول ہو گئی (Pending)"
            "IN_PROGRESS" -> "کارروائی جاری ہے (In Progress)"
            "RESOLVED" -> "حل ہو گئی (Resolved)"
            "REJECTED" -> "مسترد (Rejected)"
            else -> status
        }
    }

    fun getStatusColors(status: String): Pair<Color, Color> {
        // Return Pair(contentColor, containerColor)
        return when (status.uppercase()) {
            "PENDING" -> Pair(StatusPending, StatusPendingContainer)
            "IN_PROGRESS" -> Pair(StatusInProgress, StatusInProgressContainer)
            "RESOLVED" -> Pair(StatusResolved, StatusResolvedContainer)
            "REJECTED" -> Pair(StatusRejected, StatusRejectedContainer)
            else -> Pair(Color.DarkGray, Color.LightGray)
        }
    }

    fun getPriorityColor(priority: String): Color {
        return when (priority.uppercase()) {
            "LOW" -> PriorityLow
            "MEDIUM" -> PriorityMedium
            "HIGH" -> PriorityHigh
            "EMERGENCY" -> PriorityEmergency
            else -> PriorityMedium
        }
    }

    val chashmaGothAreas = listOf(
        "Fishermen Colony (ماہی گیر کالونی)",
        "Rehri Road Sector (ریڑھی روڈ سیکٹر)",
        "Main Bazaar & Chowk (مین بازار چوک)",
        "Jamia Masjid Noor Area (جامع مسجد نور ایریا)",
        "Govt Boys & Girls School Road (اسکول روڈ)",
        "Sector A - Coastal Belt (سیکٹر اے)",
        "Sector B - Central Goth (سیکٹر بی)",
        "Sector C - Old Goth (سیکٹر سی)",
        "Chashma Goth Entrance Gate (مین گیٹ)"
    )

    val youthWings = listOf(
        "Water Supply Youth Wing (پانی کمیٹی)",
        "Electricity Coordination Cell (بجلی ٹیم)",
        "Sanitation Youth Squad (صفائی و گٹر ٹیم)",
        "Infrastructure & Roads Wing (تعمیرات و سڑکیں)",
        "Health & Social Welfare Wing (صحت کمیٹی)",
        "Central Youth Executive Desk (مرکزی ایڈمن)"
    )
}
