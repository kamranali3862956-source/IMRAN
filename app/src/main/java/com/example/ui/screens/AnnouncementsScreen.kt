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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AnnouncementEntity
import com.example.ui.theme.OceanPrimary
import com.example.ui.theme.TealSecondary
import com.example.ui.util.AppUtils
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementsScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val announcements by viewModel.announcements.collectAsState()

    BackHandler {
        viewModel.navigateTo(AppScreen.HOME)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "اعلانات و نوٹس بورڈ",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Community Notices & Emergency Helplines",
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
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Emergency Directory Section
            item {
                Text(
                    text = "فوری رابطہ و ایمرجنسی ہیلپ لائنز (Emergency Numbers)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        HelplineRow(
                            title = "چشمہ گوٹھ ینگ جنریشن شکایت سیل",
                            subtitle = "Youth Committee General Helpline",
                            number = "03001122334",
                            icon = Icons.Default.Campaign,
                            color = OceanPrimary,
                            onCall = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:03001122334"))
                                context.startActivity(intent)
                            }
                        )

                        HelplineRow(
                            title = "پانی سپلائی و ٹینکر کوآرڈینیشن سیل",
                            subtitle = "Water Board Chashma Cell",
                            number = "02199244000",
                            icon = Icons.Default.LocalDrink,
                            color = TealSecondary,
                            onCall = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:02199244000"))
                                context.startActivity(intent)
                            }
                        )

                        HelplineRow(
                            title = "کے الیکٹرک فالٹ و ایمرجنسی",
                            subtitle = "K-Electric Bin Qasim Helpline",
                            number = "118",
                            icon = Icons.Default.ElectricBolt,
                            color = Color(0xFFF39C12),
                            onCall = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:118"))
                                context.startActivity(intent)
                            }
                        )

                        HelplineRow(
                            title = "ریسکیو 1122 و ایمبولینس",
                            subtitle = "Emergency Ambulance & Health",
                            number = "1122",
                            icon = Icons.Default.LocalHospital,
                            color = Color(0xFFE74C3C),
                            onCall = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:1122"))
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "ینگ جنریشن کے اعلانات (Announcements)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (announcements.isEmpty()) {
                item {
                    Text(
                        text = "فی الحال کوئی نیا اعلان نہیں ہے۔",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            } else {
                items(announcements, key = { it.id }) { ann ->
                    AnnouncementCard(ann = ann)
                }
            }
        }
    }
}

@Composable
private fun HelplineRow(
    title: String,
    subtitle: String,
    number: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onCall: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(text = "$subtitle ($number)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        IconButton(
            onClick = onCall,
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(color)
        ) {
            Icon(imageVector = Icons.Default.Call, contentDescription = "Call", tint = Color.White, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun AnnouncementCard(ann: AnnouncementEntity) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = TealSecondary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "📢 اہم نوٹس",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TealSecondary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = AppUtils.getTimeAgo(ann.date),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = ann.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = ann.content,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "جاری کنندہ: ${ann.author}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = OceanPrimary
            )
        }
    }
}
