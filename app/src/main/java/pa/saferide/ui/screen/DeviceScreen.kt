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

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFB6CCFF), Color(0xFFE3ECFF))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Koneksi Internet", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E88E5)
                )
            )
        },
        bottomBar = { BottomMenu(navController) },
        containerColor = Color.Transparent
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(padding)
        ) {

            // ---------------------- Dekorasi Lingkaran ----------------------
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .offset(x = (-40).dp, y = 60.dp)
                    .background(Color.White.copy(alpha = 0.25f), CircleShape)
            )

            Box(
                modifier = Modifier
                    .size(200.dp)
                    .offset(x = 190.dp, y = 220.dp)
                    .background(Color.White.copy(alpha = 0.25f), CircleShape)
            )

            // -------------------------- Konten Utama -------------------------
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(top = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    "Isi data koneksi WiFi",
                    fontSize = 20.sp,
                    color = Color(0xFF0D47A1)
                )

                Spacer(Modifier.height(20.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(12.dp, RoundedCornerShape(22.dp)),
                    colors = CardDefaults.cardColors(Color.White),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        OutlinedTextField(
                            value = ssid,
                            onValueChange = { ssid = it },
                            label = { Text("Nama Koneksi (SSID)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(16.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Kata Sandi") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(22.dp))

                        Button(
                            onClick = {
                                if (ssid.isNotBlank() && password.isNotBlank()) {
                                    isConnecting = true
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1E88E5)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Connect", fontSize = 16.sp, color = Color.White)
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                when {
                    isConnecting -> {
                        CircularProgressIndicator(color = Color(0xFF1E88E5))
                        Spacer(Modifier.height(6.dp))

                        LaunchedEffect(Unit) {
                            delay(1500)
                            isConnecting = false
                            connected = true
                        }

                        Text("Menghubungkan...", color = Color.Gray)
                    }

                    connected -> Text(
                        "✓ Terhubung ke $ssid",
                        color = Color(0xFF4CAF50),
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun BottomMenu(navController: NavController) {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("dashboard") },
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = {
                Box(
                    Modifier
                        .size(56.dp)
                        .background(Color(0xFF4A6CFF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Devices, contentDescription = null, tint = Color.White)
                }
            },
            label = { Text("Device") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("profile") },
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("Profile") }
        )
    }
}


