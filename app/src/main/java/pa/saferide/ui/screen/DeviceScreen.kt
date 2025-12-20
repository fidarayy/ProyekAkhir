package pa.saferide.ui.screen

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceScreen(navController: NavController) {

    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    val devices = remember {
        bluetoothAdapter?.bondedDevices?.toList() ?: emptyList()
    }

    var selectedDevice by remember { mutableStateOf<BluetoothDevice?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        visible = true
    }

    val background = Brush.verticalGradient(
        listOf(Color(0xFFE3F2FD), Color.White)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pilih Perangkat", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {

                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate("dashboard") {
                            popUpTo("device") { inclusive = false }
                        }
                    },
                    icon = { Icon(Icons.Default.Home, null) },
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
                            Icon(
                                Icons.Default.Devices,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    },
                    label = { Text("Device") }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate("profile") {
                            popUpTo("device") { inclusive = false }
                        }
                    },
                    icon = { Icon(Icons.Default.Person, null) },
                    label = { Text("Profile") }
                )
            }
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(background)
                .padding(paddingValues)
        ) {

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600)),
                exit = fadeOut()
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {

                    Text(
                        "Perangkat Tersedia",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0D2540)
                    )

                    Spacer(Modifier.height(16.dp))

                    if (devices.isEmpty()) {
                        Text(
                            "Tidak ada perangkat Bluetooth",
                            color = Color.Gray,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }

                    devices.forEachIndexed { index, device ->

                        var itemVisible by remember { mutableStateOf(false) }

                        LaunchedEffect(Unit) {
                            delay(index * 100L)
                            itemVisible = true
                        }

                        val scale by animateFloatAsState(
                            targetValue = if (itemVisible) 1f else 0.9f,
                            animationSpec = tween(400),
                            label = ""
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .scale(scale)
                                .clickable {
                                    selectedDevice = device
                                    showDialog = true
                                },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            elevation = CardDefaults.cardElevation(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(
                                            Color(0xFF4A6CFF),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Devices,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }

                                Spacer(Modifier.width(16.dp))

                                Column {
                                    Text(
                                        device.name ?: "Unknown Device",
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        device.address,
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ================= DIALOG =================
    if (showDialog && selectedDevice != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Konfirmasi") },
            text = {
                Text("Hubungkan ke perangkat ${selectedDevice!!.name}?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        navController.navigate(
                            "connection/${selectedDevice!!.address}"
                        )
                    }
                ) {
                    Text("Hubungkan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}
