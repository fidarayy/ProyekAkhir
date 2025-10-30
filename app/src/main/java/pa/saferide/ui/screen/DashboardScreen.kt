package pa.saferide.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage

@Composable
fun DashboardScreen(navController: NavController) {
    val profileImageUrl = remember { mutableStateOf<String?>(null) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("dashboard") },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("bluetooth") },
                    icon = { Icon(Icons.Default.Bluetooth, contentDescription = "Bluetooth") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("profile") },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            // HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "RideSafe",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Making Your Ride Safer", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Don't speed on the road, prioritize safety.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                if (profileImageUrl.value != null) {
                    AsyncImage(
                        model = profileImageUrl.value,
                        contentDescription = "Foto Profil",
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Default Profile",
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 3 BOX STAT
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DashboardStatBox("Total Perjalanan", "12", Icons.Default.DirectionsBike, Color(0xFF2196F3))
                DashboardStatBox("Device Status", "Connected", Icons.Default.Bolt, Color(0xFF4CAF50))
                DashboardStatBox("Peringatan", "0", Icons.Default.Warning, Color(0xFFFFC107))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // DEVICE HISTORY
            Text(
                "Device History",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Semua perangkat berfungsi normal ✅")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Terakhir diperiksa: 29 Okt 2025, 09:23",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

// ================= Box Komponen =================
@Composable
fun DashboardStatBox(
    title: String,
    value: String,
    icon: ImageVector,
    backgroundColor: Color
) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(100.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, color = Color.White, fontWeight = FontWeight.Bold)
            Text(title, color = Color.White, fontSize = 10.sp)
        }
    }
}


//@Composable
//fun DashboardScreen(navController: androidx.navigation.NavController) {
//    Scaffold(
//        bottomBar = {
//            NavigationBar {
//                NavigationBarItem(
//                    selected = false,
//                    onClick = { navController.navigate("dashboard") },
//                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") }
//                )
//                NavigationBarItem(
//                    selected = false,
//                    onClick = { navController.navigate("bluetooth") },
//                    icon = { Icon(Icons.Default.Bluetooth, contentDescription = "Bluetooth") }
//                )
//                NavigationBarItem(
//                    selected = false,
//                    onClick = { navController.navigate("profile") },
//                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") }
//                )
//
//            }
//        }
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .padding(paddingValues)
//                .padding(24.dp)
//                .fillMaxSize(),
//            verticalArrangement = Arrangement.Top,
//            horizontalAlignment = Alignment.Start
//        ) {
//            // ===== HEADER =====
//            Text(
//                text = "RideSafe",
//                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(
//                text = "Making Your Ride Safer",
//                style = MaterialTheme.typography.titleMedium
//            )
//            Text(
//                text = "Don't speed on the road, prioritize safety.",
//                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
//            )
//
//            Spacer(modifier = Modifier.height(32.dp))
//
//            // ===== 3 BOX STATISTIC =====
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                DashboardStatBox(
//                    title = "Total Perjalanan",
//                    value = "12",
//                    icon = Icons.Default.DirectionsBike,
//                    backgroundColor = Color(0xFF2196F3)
//                )
//                DashboardStatBox(
//                    title = "Device Status",
//                    value = "Connected",
//                    icon = Icons.Default.Bolt,
//                    backgroundColor = Color(0xFF4CAF50)
//                )
//                DashboardStatBox(
//                    title = "Peringatan",
//                    value = "0",
//                    icon = Icons.Default.Warning,
//                    backgroundColor = Color(0xFFFFC107)
//                )
//            }
//
//            Spacer(modifier = Modifier.height(32.dp))
//
//            // ===== HISTORY / DEVICE STATUS CARD =====
//            Text("Device Status", fontSize = 18.sp, fontWeight = FontWeight.Medium)
//            Spacer(modifier = Modifier.height(8.dp))
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(150.dp),
//                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
//            ) {
//                Column(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(16.dp),
//                    verticalArrangement = Arrangement.Center,
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text("Semua perangkat berfungsi normal ✅")
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Text(
//                        text = "Terakhir diperiksa: 29 Okt 2025, 09:23",
//                        style = MaterialTheme.typography.bodySmall
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun DashboardStatBox(
//    title: String,
//    value: String,
//    icon: androidx.compose.ui.graphics.vector.ImageVector,
//    backgroundColor: Color
//) {
//    Card(
//        modifier = Modifier
//            .width(100.dp)
//            .height(100.dp),
//        colors = CardDefaults.cardColors(containerColor = backgroundColor),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(8.dp),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            Icon(
//                imageVector = icon,
//                contentDescription = null,
//                tint = Color.White,
//                modifier = Modifier.size(28.dp)
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(value, color = Color.White, fontWeight = FontWeight.Bold)
//            Text(title, color = Color.White, fontSize = 10.sp)
//        }
//    }
//}