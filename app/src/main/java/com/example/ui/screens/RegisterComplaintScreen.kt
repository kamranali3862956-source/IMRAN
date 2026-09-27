package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.OceanPrimary
import com.example.ui.theme.PriorityEmergency
import com.example.ui.theme.PriorityHigh
import com.example.ui.theme.PriorityLow
import com.example.ui.theme.PriorityMedium
import com.example.ui.theme.TealSecondary
import com.example.ui.util.AppUtils
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RegisterComplaintScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    BackHandler {
        viewModel.navigateTo(AppScreen.HOME)
    }

    var selectedCategory by remember { mutableStateOf("WATER") }
    var title by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("HIGH") }
    var area by remember { mutableStateOf(currentUser?.area ?: AppUtils.chashmaGothAreas[0]) }
    var areaDropdownExpanded by remember { mutableStateOf(false) }
    var locationDetails by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var contactPhone by remember { mutableStateOf(currentUser?.phone ?: "") }
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }

    // Success dialog state
    var showSuccessDialog by remember { mutableStateOf(false) }
    var generatedTrackingCode by remember { mutableStateOf("") }

    // Android Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedPhotoUri = uri
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "نئی شکایت درج کریں",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Register Community Complaint",
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
            // Category Selection Cards
            Text(
                text = "1. شکایت کی کیٹیگری منتخب کریں (Select Category):",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val categories = listOf(
                    "WATER" to "💧 پانی کی فراہمی",
                    "ELECTRICITY" to "⚡ بجلی و پی ایم ٹی",
                    "SANITATION" to "🧹 گٹر و صفائی",
                    "ROADS" to "🛣️ سڑکیں و گلیاں",
                    "HEALTH" to "🏥 صحت و کلینک",
                    "EDUCATION" to "🏫 تعلیم و اسکول",
                    "OTHER" to "📌 دیگر مسائل"
                )

                categories.forEach { (catKey, catLabel) ->
                    val isSelected = selectedCategory == catKey
                    Surface(
                        onClick = { selectedCategory = catKey },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) OceanPrimary else MaterialTheme.colorScheme.surface,
                        shadowElevation = if (isSelected) 3.dp else 1.dp,
                        modifier = Modifier.testTag("cat_chip_$catKey")
                    ) {
                        Text(
                            text = catLabel,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // Quick suggestion chips
            Column {
                Text(
                    text = "فوری عنوان کے نمونے (Quick Suggestions):",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                val suggestions = when (selectedCategory) {
                    "WATER" -> listOf("پانی کی مین لائن پھٹ گئی", "گلی میں 4 دن سے پانی نہیں آیا", "پانی کا وال خراب ہے")
                    "ELECTRICITY" -> listOf("ٹرانسفارمر سے چنگاریاں اور کم وولٹیج", "اسٹریٹ لائٹس بند ہیں", "تاریں لٹک رہی ہیں")
                    "SANITATION" -> listOf("مین گٹر ابل رہا ہے", "کچرے کا ڈھیر لگا ہوا ہے", "نالی بند ہو گئی ہے")
                    "ROADS" -> listOf("سڑک پر گہرا گڑھا ہے", "بارش کا پانی کھڑا ہے", "گلی کی ناہموار اینٹیں")
                    else -> listOf("محلے میں فوری توجہ درکار مسئلہ", "کمیونٹی ہال کی خرابی")
                }
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    suggestions.forEach { sample ->
                        Surface(
                            onClick = { title = sample },
                            shape = RoundedCornerShape(8.dp),
                            color = TealSecondary.copy(alpha = 0.1f),
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = sample,
                                fontSize = 11.sp,
                                color = TealSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Title TextField
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("شکایت کا مختصر عنوان (Title) *") },
                placeholder = { Text("مثال: مین پانی کی لائن کا رساؤ") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("complaint_title_input")
            )

            // Priority / Urgency selection
            Column {
                Text(
                    text = "2. فوری ضرورت کی سطح (Urgency / Priority):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val priorities = listOf(
                        "LOW" to ("معمولی (Low)" to PriorityLow),
                        "MEDIUM" to ("درمیانہ (Medium)" to PriorityMedium),
                        "HIGH" to ("اہم (High)" to PriorityHigh),
                        "EMERGENCY" to ("ہنگامی (Urgent)" to PriorityEmergency)
                    )

                    priorities.forEach { (pKey, pair) ->
                        val (pLabel, pColor) = pair
                        val isSelected = priority == pKey
                        Surface(
                            onClick = { priority = pKey },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) pColor else pColor.copy(alpha = 0.1f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = pLabel.split(" ")[0],
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else pColor,
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .fillMaxWidth(),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Area and Location
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "3. چشمہ گوٹھ میں مقام و پتہ (Location Details):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                // Area Selector
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = area,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("علاقہ / سیکٹر (Area Sector) *") },
                        trailingIcon = {
                            IconButton(onClick = { areaDropdownExpanded = true }) {
                                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { areaDropdownExpanded = true }
                    )

                    DropdownMenu(
                        expanded = areaDropdownExpanded,
                        onDismissRequest = { areaDropdownExpanded = false }
                    ) {
                        AppUtils.chashmaGothAreas.forEach { areaItem ->
                            DropdownMenuItem(
                                text = { Text(areaItem, fontSize = 13.sp) },
                                onClick = {
                                    area = areaItem
                                    areaDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Landmark / Street detail
                OutlinedTextField(
                    value = locationDetails,
                    onValueChange = { locationDetails = it },
                    label = { Text("گلی نمبر، قریبی نشانی، یا پول نمبر (Street & Landmark) *") },
                    placeholder = { Text("مثال: گلی نمبر 4، قریبی مسجد نور یا دکان") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("complaint_location_input")
                )
            }

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("مسئلے کی مکمل تفصیل (Detailed Description) *") },
                placeholder = { Text("مسئلہ کب سے ہے اور کیا نقصان ہو رہا ہے، کھل کر بیان کریں...") },
                minLines = 3,
                maxLines = 6,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("complaint_description_input")
            )

            // Contact Phone
            OutlinedTextField(
                value = contactPhone,
                onValueChange = { contactPhone = it },
                label = { Text("رابطہ نمبر / واٹس ایپ (Contact Number) *") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Phone, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            // Photo Attachment (Optional)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "تصویر یا ثبوت منسلک کریں (Attach Photo)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        OutlinedButton(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("منتخب کریں", fontSize = 12.sp)
                        }
                    }

                    if (selectedPhotoUri != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            AsyncImage(
                                model = selectedPhotoUri,
                                contentDescription = "Attached photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = { selectedPhotoUri = null },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                    .size(28.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Remove", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    } else {
                        Text(
                            text = "تصویر لازمی نہیں ہے لیکن مسئلے کی تیز تر کارروائی میں مددگار ہے۔",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Submit Button
            Button(
                onClick = {
                    viewModel.submitComplaint(
                        category = selectedCategory,
                        title = title,
                        description = description,
                        locationDetails = locationDetails,
                        priority = priority,
                        photoUri = selectedPhotoUri?.toString()
                    ) { code ->
                        generatedTrackingCode = code
                        showSuccessDialog = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_complaint_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OceanPrimary),
                enabled = !isLoading && title.isNotBlank() && description.isNotBlank() && locationDetails.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text(
                        text = "شکایت درج کریں (Submit Complaint)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Success Dialog with Tracking Code
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                viewModel.navigateTo(AppScreen.HOME)
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = TealSecondary,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "شکایت کامیابی سے درج ہو گئی!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "آپ کی شکایت ینگ جنریشن چشمہ گوٹھ ایڈمن ٹیم کو موصول ہو گئی ہے۔",
                        fontSize = 13.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(
                            text = "ٹریکنگ نمبر: $generatedTrackingCode",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "آپ ہوم اسکرین پر اس شکایت کی پیشرفت چیک کر سکتے ہیں۔",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        viewModel.navigateTo(AppScreen.HOME)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OceanPrimary)
                ) {
                    Text("شکایات کی فہرست دیکھیں")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        // Share via WhatsApp
                        val message = "Chashma Goth Young Generation Complaint\nTracking ID: $generatedTrackingCode\nIssue: $title\nLocation: $locationDetails\nPlease review."
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, message)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share Complaint"))
                    }
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("واٹس ایپ پر بھیجیں", fontSize = 12.sp)
                }
            }
        )
    }
}
