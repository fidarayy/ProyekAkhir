package pa.saferide.ui.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.style.TextAlign
import pa.saferide.ui.model.UserData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardAdminScreen(
    navController: NavController,
    viewModel: DashboardAdminViewModel = viewModel()
) {
    // State
    val users by viewModel.users
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage

    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        visible = true
        viewModel.loadUsers()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Dashboard,
                            contentDescription = "Dashboard",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Dashboard Admin", color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1565C0)
                )
            )
        },
        // ===== PERBAIKAN DI SINI =====
        // GANTI custom bottom bar dengan NavigationBar
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                // Home - SELECTED
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Already on home */ },
                    icon = {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = "Home",
                            tint = Color(0xFF1976D2)
                        )
                    },
                    label = {
                        Text(
                            "Home",
                            color = Color(0xFF1976D2)
                        )
                    }
                )

                // Add User
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate("add_user") {
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0xFF1976D2))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add User",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    label = {
                        Text("Add User", color = Color.Black)
                    }
                )

                // Profile
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate("admin_profile") {
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Color.Black
                        )
                    },
                    label = {
                        Text("Profile", color = Color.Black)
                    }
                )
            }
        }
    ) { paddingValues ->
        // ... CONTENT TIDAK BERUBAH (sama seperti sebelumnya)
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(700)),
            exit = fadeOut(tween(400))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFFBBDEFB), Color.White)
                        )
                    )
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    // Header Welcome
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(4.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Selamat Datang, Admin!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color(0xFF0D47A1)
                            )
                            Text(
                                text = "Kelola semua pengguna RideSafe dari dashboard ini",
                                color = Color.Gray,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stats Card Horizontal
                    StatsCardHorizontal(users)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Error Message
                    errorMessage?.let { message ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFFEBEE)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = message,
                                    color = Color(0xFFC62828),
                                    fontSize = 14.sp
                                )
                                IconButton(
                                    onClick = { viewModel.clearError() },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Close",
                                        tint = Color(0xFFC62828)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Users List Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Daftar Pengguna",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color(0xFF0D47A1)
                            )
                            Text(
                                text = "${users.size} pengguna terdaftar",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }

                        // Refresh button kecil
                        IconButton(
                            onClick = { viewModel.loadUsers() },
                            enabled = !isLoading,
                            modifier = Modifier.size(40.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = "Refresh",
                                    tint = Color(0xFF1976D2)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Loading atau User List
                    if (isLoading && users.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Memuat data...", color = Color.Gray)
                            }
                        }
                    } else if (users.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.People,
                                    contentDescription = "No Users",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Belum ada pengguna",
                                    color = Color.Gray,
                                    fontSize = 16.sp
                                )
                                Text(
                                    "Gunakan tombol + di bawah",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(users) { user ->
                                SimpleUserCard(user, navController, viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun StatsCardHorizontal(users: List<UserData>) {
    val total = users.size
    val admins = users.count { it.role == "admin" }
    val regularUsers = users.count { it.role == "user" }
    val connected = users.count { it.connected == true }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Statistik",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF0D47A1),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItemHorizontal("Total", total, Color(0xFF1976D2))
                StatItemHorizontal("Admin", admins, Color(0xFFD32F2F))
                StatItemHorizontal("User", regularUsers, Color(0xFF388E3C))
                StatItemHorizontal("Connected", connected, Color(0xFF4CAF50))
            }
        }
    }
}

@Composable
fun StatItemHorizontal(label: String, value: Int, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun SimpleUserCard(
    user: UserData,
    navController: NavController,
    viewModel: DashboardAdminViewModel
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("user_detail/${user.uid}")
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Row 1: Avatar, Username, Role, Connection
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (user.role == "admin") Color(0xFFD32F2F)
                            else Color(0xFF1976D2)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.username.take(2).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // User Info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = user.username,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Badge(
                            containerColor = if (user.role == "admin")
                                Color(0xFFD32F2F)
                            else Color(0xFF388E3C)
                        ) {
                            Text(
                                text = user.role,
                                fontSize = 10.sp,
                                color = Color.White
                            )
                        }
                    }

                    Text(
                        text = user.email,
                        color = Color.Gray,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    // Connection Status
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (user.connected == true) Color.Green
                                    else Color.Red
                                )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (user.connected == true) "Helmet terhubung"
                            else "Helmet tidak terhubung",
                            fontSize = 12.sp,
                            color = if (user.connected == true) Color.Green
                            else Color.Gray
                        )
                    }
                }

                // Connection Toggle Button
                IconButton(
                    onClick = {
                        viewModel.updateUserConnection(
                            user.uid,
                            !(user.connected ?: false)
                        )
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = if (user.connected == true)
                            Icons.Filled.Link
                        else Icons.Filled.LinkOff,
                        contentDescription = "Toggle Connection",
                        tint = if (user.connected == true) Color.Green
                        else Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Row 2: Helmet ID (jika ada) dan Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Helmet ID
                if (user.helmetId != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Security,
                            contentDescription = "Helmet",
                            tint = Color(0xFF1976D2),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Helmet: ${user.helmetId}",
                            fontSize = 12.sp,
                            color = Color(0xFF1976D2)
                        )
                    }
                } else {
                    Text(
                        text = "Tidak ada helmet",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                // Action Buttons
                Row {
                    // Edit Button
                    IconButton(
                        onClick = {
                            navController.navigate("edit_user/${user.uid}")
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color(0xFF1976D2),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Delete Button
                    IconButton(
                        onClick = {
                            viewModel.deleteUser(user.uid)
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}