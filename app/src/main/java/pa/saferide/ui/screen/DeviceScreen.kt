package pa.saferide.ui.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
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
        colors = listOf(Color(0xFFF8F9FB), Color(0xFFE8EBF0))
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            // ======== Navigation Bar dengan animasi halus =========
            var selectedItem by remember { mutableStateOf("device") }

            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 10.dp
            ) {
                val items = listOf("dashboard", "device", "profile")
                val icons = listOf(Icons.Default.Home, Icons.Default.Devices, Icons.Default.Person)
                val labels = listOf("Home", "Device", "Profile")

                items.forEachIndexed { index, route ->
                    val selected = selectedItem == route
                    val scale by animateFloatAsState(
                        targetValue = if (selected) 1.2f else 1f,
                        animationSpec = tween(300)
                    )

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            selectedItem = route
                            when (route) {
                                "dashboard" -> navController.navigate("dashboard")
                                "device" -> {} // tetap di sini
                                "profile" -> navController.navigate("profile")
                            }
                        },
                        icon = {
                            Icon(
                                icons[index],
                                contentDescription = labels[index],
                                modifier = Modifier.scale(scale),
                                tint = if (selected) Color(0xFF4A6CFF) else Color.Gray
                            )
                        },
                        label = {
                            Text(
                                labels[index],
                                fontSize = 12.sp,
                                color = if (selected) Color(0xFF4A6CFF) else Color.Gray
                            )
                        }
                    )
                }
            }
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "Hubungkan Aplikasi dengan ESP32",
                    fontSize = 20.sp,
                    color = Color.Black
                )

                Text(
                    text = "Gunakan hotspot HP Anda sebagai sumber internet agar ESP dapat mengirim data ke aplikasi.",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
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

                if (isConnecting) {
                    LaunchedEffect(Unit) {
                        delay(2000)
                        isConnecting = false
                        connected = true
                    }
                    CircularProgressIndicator(color = Color(0xFF4A6CFF))
                    Text("Menghubungkan...", color = Color.Gray)
                }

                if (connected) {
                    Text(
                        text = "✅ Terhubung ke $ssid",
                        color = Color(0xFF4A6CFF)
                    )
                }
            }
        }
    }
}