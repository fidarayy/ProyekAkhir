package pa.saferide.ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.delay

@Composable
fun DashboardScreen(navController: NavController) {

    // ================= AUTH =================
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser ?: return
    val uid = currentUser.uid

    val username =
        currentUser.displayName
            ?: currentUser.email?.substringBefore("@")
            ?: "User"

    // ================= STATE =================
    var totalTrips by remember { mutableStateOf(0) }
    var warningCount by remember { mutableStateOf(0) }
    var lastNotif by remember { mutableStateOf("Normal") }
    var lastNotifTime by remember { mutableStateOf("—") }

    // ================= FIREBASE =================
    val db = FirebaseDatabase
        .getInstance("https://saveride-df648-default-rtdb.asia-southeast1.firebasedatabase.app/")
        .reference

    val sensorRef = db
        .child("users")
        .child(uid)
        .child("sensor")

    DisposableEffect(Unit) {

        val connectionListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                totalTrips = snapshot.childrenCount.toInt()
            }
            override fun onCancelled(error: DatabaseError) {}
        }

        val alertListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                warningCount = snapshot.childrenCount.toInt()

                val lastAlert = snapshot.children.lastOrNull()
                lastNotif = lastAlert
                    ?.child("status")
                    ?.getValue(String::class.java)
                    ?: "Normal"

                lastNotifTime = lastAlert
                    ?.child("time")
                    ?.getValue(String::class.java)
                    ?: "—"
            }
            override fun onCancelled(error: DatabaseError) {}
        }

        sensorRef.child("connection_logs")
            .addValueEventListener(connectionListener)

        sensorRef.child("alerts_logs")
            .addValueEventListener(alertListener)

        onDispose {
            sensorRef.child("connection_logs")
                .removeEventListener(connectionListener)
            sensorRef.child("alerts_logs")
                .removeEventListener(alertListener)
        }
    }

    val deviceStatusText =
        if (lastNotif == "NGANTUK")
            "Pengendara terdeteksi mengantuk ⚠️"
        else
            "Semua perangkat berfungsi normal ✅"

    val deviceStatusColor =
        if (lastNotif == "NGANTUK") Color.Red else Color(0xFF0D47A1)

    val background = Brush.verticalGradient(
        listOf(Color(0xFFEDF6FF), Color.White)
    )

    // ================= UI =================
    Scaffold(
        bottomBar = {

            // 🔥 BOTTOM NAVIGATION FIXED
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {

                // ===== HOME =====
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = {
                        Icon(Icons.Default.Home, null)
                    },
                    label = { Text("Home") }
                )

                // ===== DEVICE (TENGAH – BULAT BESAR) =====
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate("device")
                    },
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(
                                    color = Color(0xFF5B6CFF),
                                    shape = RoundedCornerShape(28.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Devices,
                                contentDescription = "Device",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    },
                    label = { Text("Device") }
                )

                // ===== PROFILE =====
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate("profile")
                    },
                    icon = {
                        Icon(Icons.Default.Person, null)
                    },
                    label = { Text("Profile") }
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(background)
                .padding(padding)
                .padding(20.dp)
        ) {

            HeaderSection(username)

            Spacer(Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                AnimatedStatBox(
                    "Total Perjalanan",
                    totalTrips.toString(),
                    Icons.Default.DirectionsBike,
                    Color(0xFF2E86FF),
                    0
                )

                AnimatedStatBox(
                    "Device Status",
                    if (lastNotif == "NGANTUK") "Warning" else "Connected",
                    Icons.Default.Bolt,
                    if (lastNotif == "NGANTUK") Color.Red else Color(0xFF43A047),
                    150
                )

                AnimatedStatBox(
                    "Peringatan",
                    warningCount.toString(),
                    Icons.Default.Warning,
                    Color(0xFFFFC107),
                    300
                )
            }

            Spacer(Modifier.height(28.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(Modifier.padding(16.dp)) {

                    Text(
                        deviceStatusText,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = deviceStatusColor
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "Status terakhir: $lastNotif",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )

                    Text(
                        "Waktu: $lastNotifTime",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

/* ================= HEADER ================= */

@Composable
fun HeaderSection(username: String) {
    Column {
        Text(
            "Halo, $username 👋",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0D2540)
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "Making Your Ride Safer",
            fontSize = 14.sp,
            color = Color(0xFF2F3B4A)
        )
    }
}

/* ================= STAT BOX ================= */

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

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.85f,
        animationSpec = tween(500),
        label = ""
    )

    Card(
        modifier = Modifier
            .width(110.dp)
            .height(110.dp)
            .scale(scale),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = Color.White)
            Spacer(Modifier.height(8.dp))
            Text(value, color = Color.White, fontWeight = FontWeight.Bold)
            Text(title, color = Color.White, fontSize = 11.sp)
        }
    }
}
