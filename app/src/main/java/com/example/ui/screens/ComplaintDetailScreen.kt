package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ComplaintEntity
import com.example.ui.theme.OceanPrimary
import com.example.ui.theme.StatusInProgress
import com.example.ui.theme.StatusPending
import com.example.ui.theme.StatusRejected
import com.example.ui.theme.StatusResolved
import com.example.ui.theme.TealSecondary
import com.example.ui.util.AppUtils
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplaintDetailScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val complaint by viewModel.selectedComplaint.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isAdmin = currentUser?.role == "ADMIN"

    BackHandler {
        viewModel.navigateTo(AppScreen.HOME)
    }

    if (complaint == null) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("شکایت نہیں مل سکی")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { viewModel.navigateTo(AppScreen.HOME) }) {
                Text("واپس جائیں")
            }
        }
        return
    }

    val currentComplaint = complaint!!

    // Admin edit state
    var editStatus by remember(currentComplaint.status) { mutableStateOf(currentComplaint.status) }
    var editRemarks by remember(currentComplaint.adminRemarks) { mutableStateOf(currentComplaint.adminRemarks) }
    var editAssignedTo by remember(currentComplaint.assignedTo) { mutableStateOf(currentComplaint.assignedTo) }
    var assignedDropdownExpanded by remember { mutableStateOf(false) }
    var editPriority by remember(currentComplaint.priority) { mutableStateOf(currentComplaint.priority) }
    var priorityDropdownExpanded by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    // Citizen feedback state
    var citizenRating by remember(currentComplaint.citizenRating) { mutableIntStateOf(if (currentComplaint.citizenRating > 0) currentComplaint.citizenRating else 5) }
    var citizenFeedbackText by remember(currentComplaint.citizenFeedback) { mutableStateOf(currentComplaint.citizenFeedback) }

    val (statusContentColor, statusContainerColor) = AppUtils.getStatusColors(currentComplaint.status)
    val priorityColor = AppUtils.getPriorityColor(currentComplaint.priority)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "تفصیل شکایت: ${currentComplaint.trackingCode}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = currentComplaint.userArea,
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
                            val shareText = "Chashma Goth Complaint #${currentComplaint.trackingCode}\nIssue: ${currentComplaint.title}\nStatus: ${currentComplaint.status}\nLocation: ${currentComplaint.locationDetails}\nAdmin Remarks: ${currentComplaint.adminRemarks}"
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Complaint"))
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OceanPrimary)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tracking Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "ٹریکنگ آئی ڈی: ${currentComplaint.trackingCode}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = statusContainerColor
                        ) {
                            Text(
                                text = AppUtils.getStatusLabel(currentComplaint.status),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusContentColor,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = currentComplaint.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = AppUtils.getCategoryIcon(currentComplaint.category),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = AppUtils.getCategoryNameUrdu(currentComplaint.category),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = priorityColor.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "ترجیح: ${currentComplaint.priority}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = priorityColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Visual Status Timeline Stepper
            StatusTimelineCard(status = currentComplaint.status)

            // Problem Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "مسئلے کی تفصیلات (Complaint Details)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = currentComplaint.description,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Photo if attached
                    if (!currentComplaint.photoUri.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "منسلک تصویر (Attached Photo):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            AsyncImage(
                                model = currentComplaint.photoUri,
                                contentDescription = "Attached photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Location detail
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = OceanPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "مقام و گلی:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${currentComplaint.userArea} - ${currentComplaint.locationDetails}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Citizen contact
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = TealSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "شکایت کنندہ (Citizen):",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${currentComplaint.userName} (${currentComplaint.userPhone})",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${currentComplaint.userPhone}")
                                }
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TealSecondary)
                        ) {
                            Icon(imageVector = Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("کال کریں", fontSize = 11.sp)
                        }
                    }

                    // Dates
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "تاریخ اندراج: ${AppUtils.formatShortDate(currentComplaint.createdAt)}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "آخری اپ ڈیٹ: ${AppUtils.getTimeAgo(currentComplaint.updatedAt)}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Official Admin Remarks & Assigned Wing Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = OceanPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ینگ جنریشن ایڈمن کارروائی اور تفویض",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Text(
                        text = "ذمہ دار کمیٹی ونگ: ${currentComplaint.assignedTo}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "ایڈمن ریمارکس / فیلڈ پیشرفت:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (currentComplaint.adminRemarks.isNotBlank()) currentComplaint.adminRemarks else "ابھی ایڈمن کی طرف سے کوئی نیا نوٹ درج نہیں کیا گیا ہے۔",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // ================== ADMIN ACTION PANEL (if user is Admin) ==================
            if (isAdmin) {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_action_panel"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = OceanPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ایڈمن کنٹرول روم (Admin Status Update)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = OceanPrimary
                            )
                        }

                        // Status Selection Chips
                        Column {
                            Text(
                                text = "اسٹیٹس تبدیل کریں (Update Status):",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val statusList = listOf(
                                    "PENDING" to ("موصول" to StatusPending),
                                    "IN_PROGRESS" to ("جاری" to StatusInProgress),
                                    "RESOLVED" to ("حل شدہ" to StatusResolved),
                                    "REJECTED" to ("مسترد" to StatusRejected)
                                )

                                statusList.forEach { (stKey, pair) ->
                                    val (stLabel, stColor) = pair
                                    val isSelected = editStatus == stKey
                                    Surface(
                                        onClick = { editStatus = stKey },
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) stColor else stColor.copy(alpha = 0.12f),
                                        modifier = Modifier.weight(1f).testTag("admin_status_$stKey")
                                    ) {
                                        Text(
                                            text = stLabel,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else stColor,
                                            modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }

                        // Assign to Youth Wing Dropdown
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = editAssignedTo,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("ذمہ دار نوجوان ونگ (Assigned Wing)") },
                                trailingIcon = {
                                    IconButton(onClick = { assignedDropdownExpanded = true }) {
                                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { assignedDropdownExpanded = true }
                            )

                            DropdownMenu(
                                expanded = assignedDropdownExpanded,
                                onDismissRequest = { assignedDropdownExpanded = false }
                            ) {
                                AppUtils.youthWings.forEach { wing ->
                                    DropdownMenuItem(
                                        text = { Text(wing, fontSize = 13.sp) },
                                        onClick = {
                                            editAssignedTo = wing
                                            assignedDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Priority Dropdown
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = editPriority,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("ترجیح (Priority)") },
                                trailingIcon = {
                                    IconButton(onClick = { priorityDropdownExpanded = true }) {
                                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { priorityDropdownExpanded = true }
                            )

                            DropdownMenu(
                                expanded = priorityDropdownExpanded,
                                onDismissRequest = { priorityDropdownExpanded = false }
                            ) {
                                listOf("LOW", "MEDIUM", "HIGH", "EMERGENCY").forEach { p ->
                                    DropdownMenuItem(
                                        text = { Text(p) },
                                        onClick = {
                                            editPriority = p
                                            priorityDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Admin Remarks TextField
                        OutlinedTextField(
                            value = editRemarks,
                            onValueChange = { editRemarks = it },
                            label = { Text("ایڈمن ریمارکس / حل کی تفصیل (Admin Remarks)") },
                            placeholder = { Text("مثال: کے ڈبلیو ایس بی وال مین کو کال کی گئی اور پائپ کی مرمت ہو گئی۔") },
                            minLines = 2,
                            maxLines = 4,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_remarks_input")
                        )

                        // Save update button
                        Button(
                            onClick = {
                                viewModel.updateComplaintByAdmin(
                                    complaintId = currentComplaint.id,
                                    newStatus = editStatus,
                                    adminRemarks = editRemarks,
                                    assignedTo = editAssignedTo,
                                    priority = editPriority
                                ) {
                                    // Saved
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("save_admin_update_btn"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OceanPrimary),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                            } else {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("اپ ڈیٹ محفوظ کریں (Save Changes)", fontWeight = FontWeight.Bold)
                            }
                        }

                        // Delete option
                        OutlinedButton(
                            onClick = { showDeleteConfirmDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("شکایت خارج کریں (Delete Complaint)", fontSize = 12.sp)
                        }
                    }
                }
            }

            // ================== CITIZEN FEEDBACK & RATING (If Resolved) ==================
            if (currentComplaint.status == "RESOLVED") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "شہری کا فیڈ بیک اور ریٹنگ (Citizen Feedback)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "کیا یہ مسئلہ تسلی بخش طریقے سے حل ہو گیا؟ اپنی رائے دیں:",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // 5 Star Rating
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (star in 1..5) {
                                IconButton(
                                    onClick = { citizenRating = star }
                                ) {
                                    Icon(
                                        imageVector = if (star <= citizenRating) Icons.Default.Star else Icons.Default.StarBorder,
                                        contentDescription = "Star $star",
                                        tint = if (star <= citizenRating) Color(0xFFFFB300) else Color.Gray,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = citizenFeedbackText,
                            onValueChange = { citizenFeedbackText = it },
                            placeholder = { Text("اپنے تاثرات یہاں درج کریں...") },
                            minLines = 2,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                viewModel.submitCitizenRating(
                                    complaintId = currentComplaint.id,
                                    rating = citizenRating,
                                    feedback = citizenFeedbackText
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TealSecondary)
                        ) {
                            Text("فیڈ بیک محفوظ کریں (Submit Feedback)")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Delete confirmation dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("شکایت خارج کریں؟") },
            text = { Text("کیا آپ واقعی شکایت نمبر ${currentComplaint.trackingCode} کو مستقل طور پر خارج کرنا چاہتے ہیں؟") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        viewModel.deleteComplaint(currentComplaint)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("خارج کریں")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("منسوخ")
                }
            }
        )
    }
}

@Composable
private fun StatusTimelineCard(status: String) {
    val currentStep = when (status.uppercase()) {
        "PENDING" -> 1
        "IN_PROGRESS" -> 2
        "RESOLVED" -> 3
        "REJECTED" -> 0
        else -> 1
    }

    val steps = listOf(
        "1. موصول (Logged)",
        "2. کام جاری (In Progress)",
        "3. حل شدہ (Resolved)"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "کارروائی کی پیشرفت (Progress Timeline):",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (status == "REJECTED") {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "یہ شکایت ایڈمن کی طرف سے مسترد کر دی گئی ہے۔ تفصیلات کے لیے نیچے ایڈمن نوٹ دیکھیں۔",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    steps.forEachIndexed { index, stepName ->
                        val stepNumber = index + 1
                        val isCompleted = stepNumber <= currentStep
                        val isCurrent = stepNumber == currentStep

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isCurrent -> OceanPrimary
                                            isCompleted -> StatusResolved
                                            else -> Color.LightGray.copy(alpha = 0.5f)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isCompleted) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                } else {
                                    Text(
                                        text = "$stepNumber",
                                        fontSize = 12.sp,
                                        color = Color.DarkGray,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stepName,
                                fontSize = 10.sp,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                color = if (isCurrent) OceanPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }

                        if (index < steps.size - 1) {
                            Box(
                                modifier = Modifier
                                    .height(2.dp)
                                    .weight(0.6f)
                                    .background(
                                        if (index + 1 < currentStep) StatusResolved else Color.LightGray.copy(alpha = 0.5f)
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}
