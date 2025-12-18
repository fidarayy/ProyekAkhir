package pa.saferide.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showUnpairDialog by remember { mutableStateOf(false) }

    // Load user jika belum ada
    LaunchedEffect(uid) {
        if (user == null) {
            viewModel.loadUsers()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detail Pengguna") },
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
                .padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {

            if (user == null) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(12.dp))
                    Text("Memuat data user...")
                }
                return@Box
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // ================= USER CARD =================
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(40.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                user.username.take(2).uppercase(),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        Text(
                            user.username,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            user.email,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(Modifier.height(12.dp))

                        // Status koneksi
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (user.connected == true)
                                    Icons.Default.CheckCircle
                                else Icons.Default.Cancel,
                                tint = if (user.connected == true)
                                    MaterialTheme.colorScheme.tertiary
                                else MaterialTheme.colorScheme.error,
                                contentDescription = null
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                if (user.connected == true)
                                    "Terhubung ke Helmet"
                                else "Tidak Terhubung"
                            )
                        }

                        user.helmetId?.takeIf { it.isNotBlank() }?.let {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Helmet ID: $it",
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // ================= ACTION BUTTONS =================
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // === Pair Helmet ===
                    if (user.connected != true || user.helmetId.isNullOrBlank()) {
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

                        // Disconnect
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

                        // Unpair
                        Button(
                            onClick = { showUnpairDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(Icons.Default.Delete, null)
                            Spacer(Modifier.width(8.dp))
                            Text("LEPAS HELMET")
                        }
                    }

                    // Edit user
                    OutlinedButton(
                        onClick = {
                            navController.navigate("edit_user/${user.uid}")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Edit, null)
                        Spacer(Modifier.width(8.dp))
                        Text("EDIT USER")
                    }

                    // Delete user
                    OutlinedButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.Default.Delete, null)
                        Spacer(Modifier.width(8.dp))
                        Text("HAPUS USER")
                    }
                }
            }

            // ================= DIALOG UNPAIR =================
            if (showUnpairDialog) {
                AlertDialog(
                    onDismissRequest = { showUnpairDialog = false },
                    title = { Text("Lepas Helmet") },
                    text = { Text("Yakin ingin melepas helmet dari user ini?") },
                    confirmButton = {
                        TextButton(onClick = {
                            showUnpairDialog = false
                            scope.launch {
                                viewModel.updateUserHelmetId(user.uid, "")
                                viewModel.updateUserConnection(user.uid, false)
                                snackbarHostState.showSnackbar("Helmet dilepas")
                            }
                        }) {
                            Text("Ya", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showUnpairDialog = false }) {
                            Text("Batal")
                        }
                    }
                )
            }

            // ================= DIALOG DELETE =================
            if (showDeleteDialog) {
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
                            Text("Hapus", color = MaterialTheme.colorScheme.error)
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
