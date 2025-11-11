package pa.saferide.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceScreen(navController: NavController) {
    var ssid by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isConnecting by remember { mutableStateOf(false) }
    var connected by remember { mutableStateOf(false) }

    // Gradient lembut
    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFEEF2FF), Color(0xFFDDE7FF))
    )

    // Warna berubah sesuai status
    val statusColor by animateColorAsState(
        targetValue = when {
            connected -> Color(0xFF4CAF50)
            isConnecting -> Color(0xFF4A6CFF)
            else -> Color.Gray
        },
        animationSpec = tween(600)
    )

    // Efek animasi "pulse" saat loading
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Koneksi Internet", color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate("dashboard") {
                            popUpTo("device") { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Default.Home, null, tint = Color.Black) },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color(0xFF4A6CFF), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Devices, null, tint = Color.White)
                        }
                    },
                    label = { Text("Device") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate("profile") {
                            popUpTo("device") { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Default.Person, null, tint = Color.Black) },
                    label = { Text("Profile") }
                )
            }
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(paddingValues)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    "Hubungkan Aplikasi dengan ESP32",
                    fontSize = 22.sp,
                    color = Color.Black
                )

                Text(
                    "Gunakan hotspot HP Anda sebagai sumber internet agar ESP dapat mengirim data ke aplikasi.",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(12.dp, RoundedCornerShape(24.dp))
                        .background(Color.White, RoundedCornerShape(24.dp))
                        .scale(if (isConnecting) scale else 1f),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = ssid,
                            onValueChange = { ssid = it },
                            label = { Text("Nama Koneksi (SSID)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Kata Sandi") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedButton(
                                onClick = {
                                    ssid = ""
                                    password = ""
                                    connected = false
                                },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFF4A6CFF)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "Reset")
                                Spacer(Modifier.width(8.dp))
                                Text("Reset")
                            }

                            Button(
                                onClick = {
                                    if (ssid.isNotBlank() && password.isNotBlank()) {
                                        isConnecting = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4A6CFF)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Connect", color = Color.White)
                            }
                        }
                    }
                }

                Crossfade(targetState = when {
                    isConnecting -> "connecting"
                    connected -> "connected"
                    else -> "idle"
                }, label = "") { state ->
                    when (state) {
                        "connecting" -> {
                            LaunchedEffect(Unit) {
                                delay(2000)
                                isConnecting = false
                                connected = true
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = Color(0xFF4A6CFF))
                                Spacer(Modifier.height(8.dp))
                                Text("Menghubungkan...", color = Color.Gray)
                            }
                        }

                        "connected" -> {
                            AnimatedVisibility(visible = true) {
                                Text(
                                    text = "✅ Terhubung ke $ssid",
                                    color = statusColor,
                                    fontSize = 16.sp
                                )
                            }
                        }

                        else -> Unit
                    }
                }
            }
        }
    }
}
