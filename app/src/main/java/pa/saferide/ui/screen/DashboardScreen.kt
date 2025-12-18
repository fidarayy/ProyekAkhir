package pa.saferide.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

@Composable
fun DashboardScreen(navController: NavController) {
    val profileImageUrl = remember { mutableStateOf<String?>(null) }

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(200)
        visible = true
    }

    // background gradient yang lembut
    val background = Brush.verticalGradient(listOf(Color(0xFFEDF6FF), Color(0xFFFFFFFF)))

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 6.dp
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { /* sudah di home */ },
                    icon = {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = "Home",
                            tint = Color(0xFF1A1A1A),
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text("Home", color = Color(0xFF1A1A1A), fontSize = 12.sp) }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate("device") {
                            popUpTo("dashboard") { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4A6CFF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Devices, contentDescription = "Device", tint = Color.White)
                        }
                    },
                    label = { Text("Device", color = Color(0xFF1A1A1A), fontSize = 12.sp) }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate("profile") {
                            popUpTo("dashboard") { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color(0xFF1A1A1A))
                    },
                    label = { Text("Profile", color = Color(0xFF1A1A1A), fontSize = 12.sp) }
                )
            }
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(background)
                .padding(paddingValues)
                .padding(20.dp)
        ) {
            // decorative circles (top-right & bottom-left)
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4A6CFF).copy(alpha = 0.10f))
                    .align(Alignment.TopEnd)
                    .offset(x = 40.dp, y = (-40).dp)
            )
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1565C0).copy(alpha = 0.08f))
                    .align(Alignment.BottomStart)
                    .offset(x = (-40).dp, y = 60.dp)
            )

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(600)) +
                        slideInVertically(initialOffsetY = { it / 6 }, animationSpec = tween(600)),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top
                ) {
                    HeaderSection(profileImageUrl.value)

                    Spacer(Modifier.height(28.dp))

                    // Statistik: buat tiga box rapi
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        AnimatedStatBox(
                            "Total Perjalanan",
                            "12",
                            Icons.Default.DirectionsBike,
                            Color(0xFF2E86FF),
                            delayMillis = 0
                        )
                        AnimatedStatBox(
                            "Device Status",
                            "Connected",
                            Icons.Default.Bolt,
                            Color(0xFF43A047),
                            delayMillis = 150
                        )
                        AnimatedStatBox(
                            "Peringatan",
                            "0",
                            Icons.Default.Warning,
                            Color(0xFFFFC107),
                            delayMillis = 300
                        )
                    }

                    Spacer(Modifier.height(28.dp))

                    // Card besar riwayat/perangkat
                    Text(
                        "Riwayat Perjalanan",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0D0D0D)
                    )

                    Spacer(Modifier.height(8.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp)
                            .shadow(8.dp, RoundedCornerShape(18.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(18.dp),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                "Semua perangkat berfungsi normal ✅",
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                                color = Color(0xFF0D47A1)
                            )
                            Spacer(Modifier.height(10.dp))
                            Text(
                                "Terakhir connect: 29 Okt 2025, 09:23",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                            Spacer(Modifier.height(16.dp))
                            // small status chips row
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                StatusChip("Last notif: Good")
                                StatusChip("Battery: 88%")
                                StatusChip("Signal: Good")
                            }
                        }
                    }
                }
            }
        }
    }
}

/* ====================== HEADER ====================== */
@Composable
fun HeaderSection(profileImageUrl: String?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column {
            Text(
                "RideSafe",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0D2540)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Making Your Ride Safer",
                fontSize = 14.sp,
                color = Color(0xFF2F3B4A)
            )
            Text(
                "Don't speed on the road, prioritize safety.",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

    }
}

/* ====================== STAT BOX ====================== */
@Composable
fun AnimatedStatBox(
    title: String,
    value: String,
    icon: ImageVector,
    backgroundColor: Color,
    delayMillis: Int
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delayMillis.toLong())
        visible = true
    }

    // scale masuk
    val scaleEnter by animateFloatAsState(
        targetValue = if (visible) 1f else 0.75f,
        animationSpec = tween(500)
    )

    // subtle floating animation
    val infinite = rememberInfiniteTransition()
    val floatOffset by infinite.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Card(
        modifier = Modifier
            .width(110.dp)
            .height(110.dp)
            .scale(scaleEnter)
            .offset(y = floatOffset.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(8.dp))
            Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(title, color = Color.White, fontSize = 11.sp)
        }
    }
}

/* ===== small status chip ===== */
@Composable
fun StatusChip(text: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F6FF)),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .wrapContentWidth()
            .height(32.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 12.dp)) {
            Text(text, color = Color(0xFF1A237E), fontSize = 12.sp)
        }
    }
}

