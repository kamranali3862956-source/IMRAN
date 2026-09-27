package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.OceanPrimary
import com.example.ui.theme.TealSecondary
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val allComplaints by viewModel.allComplaints.collectAsState()

    BackHandler {
        viewModel.navigateTo(AppScreen.HOME)
    }

    val userComplaints = allComplaints.filter { it.userId == (currentUser?.id ?: -1L) }
    val resolvedByUser = userComplaints.count { it.status == "RESOLVED" }
    val pendingByUser = userComplaints.count { it.status == "PENDING" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "صارف پروفائل (User Profile)",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
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
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(OceanPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (currentUser?.name?.take(1) ?: "U").uppercase(),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = currentUser?.name ?: "Guest User",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (currentUser?.role == "ADMIN") Color(0xFFFFD54F) else TealSecondary.copy(alpha = 0.15f),
                            modifier = Modifier.padding(top = 6.dp)
                        ) {
                            Text(
                                text = if (currentUser?.role == "ADMIN") "🛡️ نوجوان منتظم (Youth Admin)" else "👤 رجسٹرڈ شہری (Resident)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (currentUser?.role == "ADMIN") Color(0xFF3E2723) else TealSecondary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = userComplaints.size.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                Text(text = "درج شکایات", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = resolvedByUser.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TealSecondary)
                                Text(text = "حل شدہ", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = pendingByUser.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE67E22))
                                Text(text = "زیر جائزہ", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            // Contact & Area Info
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = "ذاتی معلومات (Details)", fontWeight = FontWeight.Bold, fontSize = 14.sp)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = OceanPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = "فون نمبر / واٹس ایپ", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = currentUser?.phone ?: "Not set", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = OceanPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = "چشمہ گوٹھ کا علاقہ", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = currentUser?.area ?: "Chashma Goth", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }

            // Quick Role Switcher for seamless testing & management
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = null, tint = OceanPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "فوری موڈ سوئچ (Quick Mode Switch)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Text(
                            text = "شہری اور ایڈمن کنٹرول روم دونوں چیک کرنے کے لیے سوئچ کریں:",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.quickLoginAsResident() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("شہری موڈ (Resident)", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = { viewModel.quickLoginAsAdmin() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("ایڈمن موڈ (Admin)", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // About Chashma Goth Young Generation
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = OceanPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "چشمہ گوٹھ ینگ جنریشن کے بارے میں",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "چشمہ گوٹھ ینگ جنریشن ایک غیر سیاسی، فلاحی نوجوان تحریک ہے جس کا مقصد گوٹھ کے مکینوں کو صاف پانی، بجلی کے فالٹس کے سدباب، سیوریج اور بنیادی سہولیات کی فراہمی میں بااختیار بنانا ہے۔ تمام شکایات نوجوان رضا کار کمیٹی براہ راست متعلقہ محکموں (KWSB, K-Electric, Union Council) کے ساتھ مل کر حل کرواتی ہے۔",
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Logout Button
            item {
                Button(
                    onClick = { viewModel.logout() },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC0392B))
                ) {
                    Icon(imageVector = Icons.Default.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("لاگ آؤٹ کریں (Sign Out)", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
