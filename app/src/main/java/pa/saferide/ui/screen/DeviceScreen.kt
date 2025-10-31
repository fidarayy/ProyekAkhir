package pa.saferide.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DeviceScreen(navController: NavController) {
    var showDeviceList by remember { mutableStateOf(false) }
    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFEEF3FF), Color(0xFFDCE6FF))
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("dashboard") },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Stay here */ },
                    icon = { Icon(Icons.Default.Bluetooth, contentDescription = "Device") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("profile") },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = showDeviceList,
                transitionSpec = {
                    slideInHorizontally(initialOffsetX = { it }) + fadeIn() with
                            slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
                }
            ) { showList ->
                if (showList) {
                    BluetoothListScreen(onBack = { showDeviceList = false })
                } else {
                    DeviceHomeScreen(onFindDevice = { showDeviceList = true })
                }
            }
        }
    }
}

@Composable
fun DeviceHomeScreen(onFindDevice: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 }),
        exit = fadeOut()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "SafeRide",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Making Your Ride Safer",
                style = MaterialTheme.typography.titleMedium.copy(color = Color.DarkGray)
            )

            Spacer(modifier = Modifier.height(40.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Connect to Device",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    AnimatedButton(
                        text = "Find Device",
                        color = Color(0xFF4A6CFF),
                        onClick = onFindDevice
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    AnimatedButton(
                        text = "Connect",
                        color = Color(0xFF9AA9FF),
                        onClick = { showDialog = true }
                    )
                }
            }
        }
    }

    // 🔔 Alert Dialog tampil saat tombol "Connect" ditekan
    if (showDialog) {
        AnimatedDialog(
            title = "Are you sure?",
            message = "Do you want to connect to this Bluetooth device?",
            onDismiss = { showDialog = false },
            onConfirm = {
                showDialog = false
                // Tambahkan aksi koneksi Bluetooth asli di sini nanti
            }
        )
    }
}

@Composable
fun BluetoothListScreen(onBack: () -> Unit) {
    val fakeDevices = listOf(
        "OBD-II Sensor 1",
        "Helmet Safety Link",
        "Heart Rate Monitor",
        "SafeRide Module A",
        "Bluetooth Tracker B"
    )

    var connectedDevice by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Available Devices",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(fakeDevices) { device ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (connectedDevice == device)
                            Color(0xFFDCE6FF) else Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            connectedDevice = device
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(
                                        color = Color(0xFF4A6CFF).copy(alpha = 0.1f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Bluetooth,
                                    contentDescription = null,
                                    tint = Color(0xFF4A6CFF)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = device,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        if (connectedDevice == device) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Connected",
                                tint = Color(0xFF4A6CFF)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(initialScale = 0.8f) + fadeIn(),
        exit = scaleOut(targetScale = 0.8f) + fadeOut()
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF1A1A1A)
                )
            },
            text = {
                Text(
                    message,
                    fontSize = 16.sp,
                    color = Color(0xFF5A5A5A)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirm,
                    modifier = Modifier.background(
                        color = Color(0xFF4A6CFF),
                        shape = RoundedCornerShape(8.dp)
                    )
                ) {
                    Text("Yes", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("No", color = Color.Gray, fontWeight = FontWeight.Medium)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 8.dp
        )
    }
}

@Composable
fun AnimatedButton(text: String, color: Color, onClick: (() -> Unit)? = null) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = tween(100)
    )

    Button(
        onClick = {
            pressed = true
            onClick?.invoke()
        },
        colors = ButtonDefaults.buttonColors(containerColor = color),
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { pressed = true },
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text, color = Color.White, fontSize = 16.sp)
    }

    LaunchedEffect(pressed) {
        if (pressed) {
            delay(120)
            pressed = false
        }
    }
}

//@Composable
//fun DeviceScreen(navController: NavController) {
//    val gradient = Brush.verticalGradient(
//        colors = listOf(Color(0xFFEEF3FF), Color(0xFFDCE6FF))
//    )
//
//    // State animasi: muncul perlahan setelah layar terbuka
//    var visible by remember { mutableStateOf(false) }
//    LaunchedEffect(Unit) {
//        delay(150) // delay kecil biar smooth
//        visible = true
//    }
//
//    Scaffold(
//        bottomBar = {
//            NavigationBar {
//                NavigationBarItem(
//                    selected = false,
//                    onClick = { navController.navigate("dashboard") },
//                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") }
//                )
//                NavigationBarItem(
//                    selected = true,
//                    onClick = { /* stay here */ },
//                    icon = { Icon(Icons.Default.Bluetooth, contentDescription = "Device") }
//                )
//                NavigationBarItem(
//                    selected = false,
//                    onClick = { navController.navigate("profile") },
//                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") }
//                )
//            }
//        }
//    ) { padding ->
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(gradient)
//                .padding(padding),
//            contentAlignment = Alignment.Center
//        ) {
//            AnimatedVisibility(
//                visible = visible,
//                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 }),
//                exit = fadeOut()
//            ) {
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    verticalArrangement = Arrangement.Center,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(24.dp)
//                ) {
//                    // Judul
//                    Text(
//                        text = "SafeRide",
//                        style = MaterialTheme.typography.headlineMedium.copy(
//                            fontWeight = FontWeight.Bold,
//                            color = Color.Black
//                        )
//                    )
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Text(
//                        text = "Making Your Ride Safer",
//                        style = MaterialTheme.typography.titleMedium.copy(color = Color.DarkGray)
//                    )
//
//                    Spacer(modifier = Modifier.height(40.dp))
//
//                    // Kartu utama
//                    Card(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(250.dp),
//                        colors = CardDefaults.cardColors(containerColor = Color.White),
//                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
//                        shape = RoundedCornerShape(24.dp)
//                    ) {
//                        Column(
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .padding(24.dp),
//                            horizontalAlignment = Alignment.CenterHorizontally,
//                            verticalArrangement = Arrangement.Center
//                        ) {
//                            Text(
//                                text = "Connect to Device",
//                                style = MaterialTheme.typography.titleMedium.copy(
//                                    color = Color.Gray,
//                                    fontWeight = FontWeight.Medium
//                                )
//                            )
//                            Spacer(modifier = Modifier.height(24.dp))
//
//                            AnimatedButton(text = "Find Device", color = Color(0xFF4A6CFF))
//                            Spacer(modifier = Modifier.height(16.dp))
//                            AnimatedButton(text = "Connect", color = Color(0xFF9AA9FF))
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun AnimatedButton(text: String, color: Color) {
//    var pressed by remember { mutableStateOf(false) }
//    val scale by animateFloatAsState(targetValue = if (pressed) 0.95f else 1f)
//
//    Button(
//        onClick = { pressed = true },
//        colors = ButtonDefaults.buttonColors(containerColor = color),
//        modifier = Modifier
//            .fillMaxWidth()
//            .scale(scale)
//            .clickable(
//                indication = null,
//                interactionSource = remember { MutableInteractionSource() }
//            ) {
//                pressed = true
//            },
//        shape = RoundedCornerShape(12.dp)
//    ) {
//        Text(text, color = Color.White, fontSize = 16.sp)
//    }
//
//    // Setelah animasi scale aktif sebentar, kembali ke ukuran normal
//    LaunchedEffect(pressed) {
//        if (pressed) {
//            delay(120)
//            pressed = false
//        }
//    }
//}