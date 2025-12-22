package pa.saferide.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
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
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProfileScreen(navController: NavController) {

    // ================= AUTH =================
    val auth = FirebaseAuth.getInstance()
    val adminUid = auth.currentUser?.uid ?: return

    // ================= STATE =================
    var adminName by remember { mutableStateOf("Loading...") }
    var adminEmail by remember { mutableStateOf("-") }
    var adminRole by remember { mutableStateOf("Admin") }

    // ================= DATABASE =================
    LaunchedEffect(Unit) {
        val dbRef = FirebaseDatabase.getInstance()
            .getReference("admins")
            .child(adminUid)

        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                adminName = snapshot.child("name").getValue(String::class.java) ?: "Admin"
                adminEmail = snapshot.child("email").getValue(String::class.java) ?: "-"
                adminRole = snapshot.child("role").getValue(String::class.java) ?: "Admin"
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // ================= UI =================
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil Admin") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {

                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate("dashboard_admin") {
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )

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
                                tint = Color.White
                            )
                        }
                    },
                    label = { Text("Add User") }
                )

                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Color(0xFF1976D2)
                        )
                    },
                    label = { Text("Profile") }
                )
            }
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFE3F2FD), Color.White)
                    )
                )
                .padding(padding)
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // ================= FOTO PROFIL =================
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1976D2)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Admin Photo",
                        tint = Color.White,
                        modifier = Modifier.size(60.dp)
                    )
                }

                // ================= DATA ADMIN =================
                Text(
                    text = adminName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Role: $adminRole",
                    fontSize = 16.sp,
                    color = Color.DarkGray
                )

                Text(
                    text = adminEmail,
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        navController.navigate("edit_admin")
                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1976D2)
                    ),
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Text("Edit Data Admin", color = Color.White)
                }
            }
        }
    }
}
