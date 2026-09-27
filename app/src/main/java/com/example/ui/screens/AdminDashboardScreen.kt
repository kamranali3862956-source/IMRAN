package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun AdminDashboardScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allComplaints by viewModel.allComplaints.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()

    BackHandler {
        viewModel.navigateTo(AppScreen.HOME)
    }

    var selectedAdminFilter by remember { mutableStateOf("ALL") }
    var showNewAnnouncementDialog by remember { mutableStateOf(false) }

    // Announcement input state
    var annTitle by remember { mutableStateOf("") }
    var annContent by remember { mutableStateOf("") }
    var annCategory by remember { mutableStateOf("WATER_SUPPLY") }

    val total = allComplaints.size
    val pending = allComplaints.count { it.status == "PENDING" }
    val inProgress = allComplaints.count { it.status == "IN_PROGRESS" }
    val resolved = allComplaints.count { it.status == "RESOLVED" }
    val resolutionRate = if (total > 0) (resolved * 100 / total) else 0

    val filteredList = when (selectedAdminFilter) {
        "PENDING" -> allComplaints.filter { it.status == "PENDING" }
        "IN_PROGRESS" -> allComplaints.filter { it.status == "IN_PROGRESS" }
        "RESOLVED" -> allComplaints.filter { it.status == "RESOLVED" }
        else -> allComplaints
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "ایڈمن ڈیش بورڈ و کنٹرول روم",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Chashma Goth Youth Executive Committee",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(AppScreen.HOME) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val summary = "Chashma Goth Complaint Summary:\nTotal: $total\nPending: $pending\nIn Progress: $inProgress\nResolved: $resolved\nResolution Rate: $resolutionRate%"
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, summary)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Summary Report"))
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share Report", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OceanPrimary)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // KPI Summary Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = OceanPrimary),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "شکایات کی مجموعی صورتحال",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "حل کی شرح: $resolutionRate% کامیابی",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            AdminKpiBox(label = "کل شکایات", value = total.toString(), color = Color.White)
                            AdminKpiBox(label = "زیر جائزہ", value = pending.toString(), color = Color(0xFFFFCC80))
                            AdminKpiBox(label = "جاری کام", value = inProgress.toString(), color = Color(0xFF90CAF9))
                            AdminKpiBox(label = "حل شدہ", value = resolved.toString(), color = Color(0xFFA5D6A7))
                        }
                    }
                }
            }

            // Quick Actions Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { showNewAnnouncementDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_post_announcement_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TealSecondary)
                    ) {
                        Icon(imageVector = Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("نیا اعلان شائع کریں", fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Status Filter Tabs for Admin
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val tabs = listOf(
                        "ALL" to "تمام شکایات ($total)",
                        "PENDING" to "⚠️ زیر جائزہ ($pending)",
                        "IN_PROGRESS" to "🔧 جاری کام ($inProgress)",
                        "RESOLVED" to "✅ حل شدہ ($resolved)"
                    )
                    items(tabs) { (key, label) ->
                        FilterChip(
                            selected = selectedAdminFilter == key,
                            onClick = { selectedAdminFilter = key },
                            label = { Text(label, fontSize = 12.sp) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Complaints for Admin
            items(filteredList, key = { it.id }) { complaint ->
                ComplaintCard(
                    complaint = complaint,
                    isAdmin = true,
                    onClick = { viewModel.viewComplaintDetails(complaint) }
                )
            }

            // Registered Citizens Overview
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Group, contentDescription = null, tint = OceanPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "رجسٹرڈ شہری و ممبران (${allUsers.size})",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        allUsers.forEach { user ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = user.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(
                                        text = "${user.area} • ${user.phone}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (user.role == "ADMIN") OceanPrimary.copy(alpha = 0.15f) else Color.LightGray.copy(alpha = 0.3f)
                                ) {
                                    Text(
                                        text = user.role,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (user.role == "ADMIN") OceanPrimary else Color.DarkGray,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                IconButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_DIAL).apply {
                                            data = Uri.parse("tel:${user.phone}")
                                        }
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Call,
                                        contentDescription = "Call",
                                        tint = TealSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // New Announcement Dialog
    if (showNewAnnouncementDialog) {
        AlertDialog(
            onDismissRequest = { showNewAnnouncementDialog = false },
            title = {
                Text(
                    text = "نیا کمیونٹی اعلان شائع کریں",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = annTitle,
                        onValueChange = { annTitle = it },
                        label = { Text("اعلان کا عنوان (Title)") },
                        placeholder = { Text("مثال: پینے کے پانی کا شیڈول") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = annContent,
                        onValueChange = { annContent = it },
                        label = { Text("مکمل پیغام (Message Content)") },
                        placeholder = { Text("اہل چشمہ گوٹھ کو مطلع کیا جاتا ہے کہ...") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addAnnouncement(annTitle, annContent, annCategory) {
                            showNewAnnouncementDialog = false
                            annTitle = ""
                            annContent = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OceanPrimary),
                    enabled = annTitle.isNotBlank() && annContent.isNotBlank()
                ) {
                    Text("شائع کریں (Post)")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showNewAnnouncementDialog = false }) {
                    Text("منسوخ")
                }
            }
        )
    }
}

@Composable
private fun AdminKpiBox(
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = color)
        Text(text = label, fontSize = 11.sp, color = Color.White.copy(alpha = 0.85f))
    }
}
