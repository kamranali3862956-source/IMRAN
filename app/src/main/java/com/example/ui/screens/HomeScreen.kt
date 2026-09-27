package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ComplaintEntity
import com.example.ui.theme.OceanPrimary
import com.example.ui.theme.StatusInProgress
import com.example.ui.theme.StatusPending
import com.example.ui.theme.StatusResolved
import com.example.ui.theme.TealSecondary
import com.example.ui.util.AppUtils
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val allComplaints by viewModel.allComplaints.collectAsState()
    val isAdmin = currentUser?.role == "ADMIN"

    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedStatus by remember { mutableStateOf("ALL") }
    var selectedCategory by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }

    // Filter complaints according to role, search, status, and category
    val relevantComplaints = remember(allComplaints, currentUser, selectedTab, selectedStatus, selectedCategory, searchQuery) {
        val baseList = if (isAdmin || selectedTab == 1) {
            allComplaints
        } else {
            allComplaints.filter { it.userId == (currentUser?.id ?: -1L) }
        }

        baseList.filter { complaint ->
            val matchesStatus = selectedStatus == "ALL" || complaint.status == selectedStatus
            val matchesCategory = selectedCategory == "ALL" || complaint.category == selectedCategory
            val matchesSearch = searchQuery.isBlank() ||
                    complaint.trackingCode.contains(searchQuery, ignoreCase = true) ||
                    complaint.title.contains(searchQuery, ignoreCase = true) ||
                    complaint.locationDetails.contains(searchQuery, ignoreCase = true) ||
                    complaint.description.contains(searchQuery, ignoreCase = true)

            matchesStatus && matchesCategory && matchesSearch
        }
    }

    // Stats
    val totalCount = allComplaints.size
    val pendingCount = allComplaints.count { it.status == "PENDING" }
    val inProgressCount = allComplaints.count { it.status == "IN_PROGRESS" }
    val resolvedCount = allComplaints.count { it.status == "RESOLVED" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_app_icon),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Chashma Goth",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Young Generation Portal",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                },
                actions = {
                    // Role badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isAdmin) Color(0xFFFFD54F) else Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Text(
                            text = if (isAdmin) "🛡️ Admin" else "👤 Resident",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isAdmin) Color(0xFF3E2723) else Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.logout() },
                        modifier = Modifier.testTag("logout_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Logout",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = OceanPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.navigateTo(AppScreen.REGISTER_COMPLAINT) },
                containerColor = TealSecondary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("register_complaint_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Complaint")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("شکایت درج کریں", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Hero Community Banner Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.hero_community),
                            contentDescription = "Chashma Goth Community Banner",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.85f)
                                        )
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(14.dp)
                        ) {
                            Text(
                                text = "چشمہ گوٹھ ینگ جنریشن شکایت سیل",
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "پانی، بجلی، صفائی اور گلیوں کے مسائل کے فوری حل کے لیے",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Quick Stats Row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        title = "کل شکایات",
                        count = totalCount,
                        color = OceanPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "زیر جائزہ",
                        count = pendingCount,
                        color = StatusPending,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "کام جاری",
                        count = inProgressCount,
                        color = StatusInProgress,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "حل شدہ",
                        count = resolvedCount,
                        color = StatusResolved,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Urgent Helpline Quick Action
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(OceanPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "ہیلپ لائن: 0300-1122334",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "ینگ جنریشن ایمرجنسی شکایت ڈیسک",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }

                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:03001122334")
                                }
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OceanPrimary),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("کال کریں", fontSize = 11.sp)
                        }
                    }
                }
            }

            // Quick Hub Navigation Tabs
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickNavButton(
                        label = "نوٹس بورڈ",
                        icon = Icons.Default.Campaign,
                        onClick = { viewModel.navigateTo(AppScreen.ANNOUNCEMENTS) },
                        modifier = Modifier.weight(1f)
                    )
                    if (isAdmin) {
                        QuickNavButton(
                            label = "ایڈمن ڈیش بورڈ",
                            icon = Icons.Default.AdminPanelSettings,
                            color = OceanPrimary,
                            onClick = { viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    QuickNavButton(
                        label = "پروفائل و رابطہ",
                        icon = Icons.Default.Person,
                        onClick = { viewModel.navigateTo(AppScreen.PROFILE) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Complaints Section Title & Tabs
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = if (isAdmin) "تمام شکایات کا جائزہ (Admin Overview)" else "میری درج شدہ شکایات (My Complaints)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "اپنی شکایت کی پیشرفت چیک کریں یا نیا مسئلہ درج کروائیں",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("شکایت نمبر یا مقام تلاش کریں... (e.g. CG-2026)") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Status Filter Chips
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val statusOptions = listOf(
                        "ALL" to "سب (All)",
                        "PENDING" to "موصول (Pending)",
                        "IN_PROGRESS" to "جاری (In Progress)",
                        "RESOLVED" to "حل شدہ (Resolved)",
                        "REJECTED" to "مسترد (Rejected)"
                    )
                    items(statusOptions) { (key, label) ->
                        FilterChip(
                            selected = selectedStatus == key,
                            onClick = { selectedStatus = key },
                            label = { Text(label, fontSize = 12.sp) }
                        )
                    }
                }
            }

            // Category Filter Chips
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val categoryOptions = listOf(
                        "ALL" to "تمام کیٹیگریز",
                        "WATER" to "💧 پانی",
                        "ELECTRICITY" to "⚡ بجلی",
                        "SANITATION" to "🧹 صفائی",
                        "ROADS" to "🛣️ سڑکیں",
                        "HEALTH" to "🏥 صحت",
                        "OTHER" to "📌 دیگر"
                    )
                    items(categoryOptions) { (key, label) ->
                        FilterChip(
                            selected = selectedCategory == key,
                            onClick = { selectedCategory = key },
                            label = { Text(label, fontSize = 11.sp) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Complaints List or Empty State
            if (relevantComplaints.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.PendingActions,
                                contentDescription = null,
                                modifier = Modifier.size(54.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "کوئی شکایت نہیں ملی",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "اس فلٹر کے مطابق فی الحال کوئی شکایت موجود نہیں ہے۔",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.navigateTo(AppScreen.REGISTER_COMPLAINT) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = TealSecondary)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("نئی شکایت درج کریں")
                            }
                        }
                    }
                }
            } else {
                items(relevantComplaints, key = { it.id }) { complaint ->
                    ComplaintCard(
                        complaint = complaint,
                        isAdmin = isAdmin,
                        onClick = { viewModel.viewComplaintDetails(complaint) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Text(
                text = title,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun QuickNavButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    color: Color = TealSecondary,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.12f),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun ComplaintCard(
    complaint: ComplaintEntity,
    isAdmin: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (statusContentColor, statusContainerColor) = AppUtils.getStatusColors(complaint.status)
    val priorityColor = AppUtils.getPriorityColor(complaint.priority)

    ElevatedCard(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("complaint_card_${complaint.trackingCode}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Tracking Code, Category, Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tracking code & Priority
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = complaint.trackingCode,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = priorityColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = complaint.priority,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = priorityColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = statusContainerColor
                ) {
                    Text(
                        text = AppUtils.getStatusLabel(complaint.status),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusContentColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title with Category Icon
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = AppUtils.getCategoryIcon(complaint.category),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = complaint.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = AppUtils.getCategoryNameUrdu(complaint.category),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Description snippet
            Text(
                text = complaint.description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Location & Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${complaint.userArea} • ${complaint.locationDetails}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = AppUtils.getTimeAgo(complaint.createdAt),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Admin remarks highlight if present
            if (complaint.adminRemarks.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ایڈمن کا جواب: ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = complaint.adminRemarks,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Citizen rating display if resolved and rated
            if (complaint.status == "RESOLVED" && complaint.citizenRating > 0) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "شہری کا فیڈ بیک: ", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    repeat(complaint.citizenRating) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
