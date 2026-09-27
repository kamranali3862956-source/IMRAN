package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.OceanPrimary
import com.example.ui.theme.TealSecondary
import com.example.ui.util.AppUtils
import com.example.ui.viewmodel.AppViewModel

@Composable
fun AuthScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Login, 1: Register
    val authError by viewModel.authError.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Login Form State
    var loginIdentifier by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var loginPasswordVisible by remember { mutableStateOf(false) }

    // Register Form State
    var regName by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regPasswordVisible by remember { mutableStateOf(false) }
    var regArea by remember { mutableStateOf(AppUtils.chashmaGothAreas[0]) }
    var areaDropdownExpanded by remember { mutableStateOf(false) }
    var regRole by remember { mutableStateOf("RESIDENT") } // "RESIDENT" or "ADMIN"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Hero Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            OceanPrimary,
                            TealSecondary
                        )
                    )
                )
                .padding(top = 28.dp, bottom = 24.dp, start = 20.dp, end = 20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Circular App Emblem / Logo
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_app_icon),
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "CHASHMA GOTH",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )

                Text(
                    text = "Young Generation Complaints",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "چشمہ گوٹھ عوامی شکایات و حل پورٹل",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Quick One-Click Demo Access Bar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "فوری ٹیسٹ لاگ ان (Quick Demo Access)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.quickLoginAsResident() },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_resident_login_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TealSecondary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Resident (شہری)", fontSize = 12.sp)
                    }

                    Button(
                        onClick = { viewModel.quickLoginAsAdmin() },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_admin_login_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OceanPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Admin (ایڈمن)", fontSize = 12.sp)
                    }
                }
            }
        }

        // Auth Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("لاگ ان (Sign In)", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("نیا اکاؤنٹ (Register)", fontWeight = FontWeight.Bold) }
            )
        }

        // Error message banner
        AnimatedVisibility(visible = authError != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = authError ?: "",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp),
                    fontSize = 13.sp
                )
            }
        }

        // Form Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (selectedTab == 0) {
                    // =================== LOGIN FORM ===================
                    Text(
                        text = "اپنے اکاؤنٹ میں لاگ ان کریں",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    OutlinedTextField(
                        value = loginIdentifier,
                        onValueChange = { loginIdentifier = it },
                        label = { Text("فون نمبر یا ای میل (Phone or Email)") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Phone, contentDescription = null)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_identifier_input")
                    )

                    OutlinedTextField(
                        value = loginPassword,
                        onValueChange = { loginPassword = it },
                        label = { Text("پاس ورڈ (Password)") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Key, contentDescription = null)
                        },
                        trailingIcon = {
                            IconButton(onClick = { loginPasswordVisible = !loginPasswordVisible }) {
                                Icon(
                                    imageVector = if (loginPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle password"
                                )
                            }
                        },
                        visualTransformation = if (loginPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_password_input")
                    )

                    Button(
                        onClick = { viewModel.login(loginIdentifier, loginPassword) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("login_submit_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OceanPrimary),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        } else {
                            Text("لاگ ان کریں (Sign In)", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Text(
                        text = "ڈیمو معلومات: فون 03001234567 | پاس ورڈ user123",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                } else {
                    // =================== REGISTER FORM ===================
                    Text(
                        text = "نیا اکاؤنٹ بنائیں (Create Account)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    OutlinedTextField(
                        value = regName,
                        onValueChange = { regName = it },
                        label = { Text("پورا نام (Full Name) *") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reg_name_input")
                    )

                    OutlinedTextField(
                        value = regPhone,
                        onValueChange = { regPhone = it },
                        label = { Text("موبائل / واٹس ایپ نمبر (Phone / WhatsApp) *") },
                        placeholder = { Text("0300-1234567") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Phone, contentDescription = null)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reg_phone_input")
                    )

                    OutlinedTextField(
                        value = regEmail,
                        onValueChange = { regEmail = it },
                        label = { Text("ای میل (Email - اختیاری)") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Email, contentDescription = null)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Area dropdown
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = regArea,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("چشمہ گوٹھ کا علاقہ / محلہ (Area) *") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null)
                            },
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
                            onDismissRequest = { areaDropdownExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.85f)
                        ) {
                            AppUtils.chashmaGothAreas.forEach { areaItem ->
                                DropdownMenuItem(
                                    text = { Text(areaItem, fontSize = 13.sp) },
                                    onClick = {
                                        regArea = areaItem
                                        areaDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = regPassword,
                        onValueChange = { regPassword = it },
                        label = { Text("پاس ورڈ (Password) *") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Key, contentDescription = null)
                        },
                        trailingIcon = {
                            IconButton(onClick = { regPasswordVisible = !regPasswordVisible }) {
                                Icon(
                                    imageVector = if (regPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (regPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reg_password_input")
                    )

                    // Role selection chips
                    Column {
                        Text(
                            text = "اکاؤنٹ کی قسم (Account Role):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val isResident = regRole == "RESIDENT"
                            OutlinedButton(
                                onClick = { regRole = "RESIDENT" },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isResident) TealSecondary.copy(alpha = 0.15f) else Color.Transparent
                                ),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = Brush.horizontalGradient(
                                        listOf(
                                            if (isResident) TealSecondary else Color.LightGray,
                                            if (isResident) TealSecondary else Color.LightGray
                                        )
                                    )
                                )
                            ) {
                                Text(
                                    text = "شہری (Citizen)",
                                    fontSize = 12.sp,
                                    fontWeight = if (isResident) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isResident) TealSecondary else MaterialTheme.colorScheme.onSurface
                                )
                            }

                            val isAdmin = regRole == "ADMIN"
                            OutlinedButton(
                                onClick = { regRole = "ADMIN" },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isAdmin) OceanPrimary.copy(alpha = 0.15f) else Color.Transparent
                                ),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = Brush.horizontalGradient(
                                        listOf(
                                            if (isAdmin) OceanPrimary else Color.LightGray,
                                            if (isAdmin) OceanPrimary else Color.LightGray
                                        )
                                    )
                                )
                            ) {
                                Text(
                                    text = "منتظم (Youth Admin)",
                                    fontSize = 12.sp,
                                    fontWeight = if (isAdmin) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isAdmin) OceanPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.register(
                                name = regName,
                                phone = regPhone,
                                email = regEmail,
                                password = regPassword,
                                area = regArea,
                                role = regRole
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("reg_submit_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OceanPrimary),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        } else {
                            Text("اکاؤنٹ بنائیں (Register)", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
