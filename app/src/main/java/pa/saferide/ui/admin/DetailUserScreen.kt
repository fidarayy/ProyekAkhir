package pa.saferide.ui.admin

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailUserScreen(
    uid: String,
    navController: NavController,
    viewModel: DashboardAdminViewModel = viewModel()
) {
    val users by viewModel.users
    val user = users.find { it.uid == uid }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showUnpairDialog by remember { mutableStateOf(false) }

    // Load user sekali saja
    LaunchedEffect(Unit) {
        if (users.isEmpty()) {
            viewModel.loadUsers()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Pengguna", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
        ) {

            if (user == null) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(12.dp))
                    Text("Memuat data pengguna...")
                }
                return@Box
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // ================= USER CARD =================
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1976D2)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user.username.take(2).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        Text(
                            user.username,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )

                        Text(
                            user.email,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )

                        Spacer(Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (user.connected)
                                    Icons.Default.CheckCircle
                                else Icons.Default.Cancel,
                                tint = if (user.connected)
                                    Color(0xFF2E7D32)
                                else Color(0xFFD32F2F),
                                contentDescription = null
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                if (user.connected)
                                    "Helmet terhubung"
                                else "Helmet tidak terhubung"
                            )
                        }



                        // ================= ACTION BUTTONS =================
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            val hasHelmet = !user.helmetId.isNullOrBlank()

                            if (!user.connected || !hasHelmet) {
                                val hasHelmet = !user.helmetId.isNullOrBlank()

                                if (!user.connected || !hasHelmet) {

                                    Button(
                                        onClick = {
                                            navController.navigate("bluetooth_device/${user.uid}")
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.Link, null)
                                        Spacer(Modifier.width(8.dp))
                                        Text("PASANGKAN HELMET")
                                    }
                                } else {

                                    OutlinedButton(
                                        onClick = {
                                            viewModel.updateUserConnection(user.uid, false)
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Koneksi diputus")
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.LinkOff, null)
                                        Spacer(Modifier.width(8.dp))
                                        Text("PUTUSKAN KONEKSI")
                                    }

                                    Button(
                                        onClick = { showUnpairDialog = true },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFD32F2F)
                                        )
                                    ) {
                                        Icon(Icons.Default.Delete, null)
                                        Spacer(Modifier.width(8.dp))
                                        Text("LEPAS HELMET")
                                    }
                                }
                                OutlinedButton(
                                    onClick = { showDeleteDialog = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color(0xFFD32F2F)
                                    )
                                ) {
                                    Icon(Icons.Default.Delete, null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("HAPUS USER")
                                }
                            }
                        }
                    }
                }

                // ================= DELETE DIALOG =================
                if (showDeleteDialog && user != null) {
                    AlertDialog(
                        onDismissRequest = { showDeleteDialog = false },
                        title = { Text("Hapus User") },
                        text = { Text("Aksi ini tidak bisa dibatalkan") },
                        confirmButton = {
                            TextButton(onClick = {
                                showDeleteDialog = false
                                scope.launch {
                                    viewModel.deleteUser(user.uid)
                                    snackbarHostState.showSnackbar("User dihapus")
                                    delay(600)
                                    navController.navigateUp()
                                }
                            }) {
                                Text("Hapus", color = Color(0xFFD32F2F))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDeleteDialog = false }) {
                                Text("Batal")
                            }
                        }
                    )
                }
            }
        }
    }
}