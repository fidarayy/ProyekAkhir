package pa.saferide.ui.admin

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BluetoothDeviceScreen(navController: NavController) {
    val context = LocalContext.current
    val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var pairedDevices by remember { mutableStateOf<List<BluetoothDevice>>(emptyList()) }

    // --- Permission Launcher untuk Android 12 ke atas ---
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { result ->
            val granted = result.values.all { it }
            if (granted) {
                scope.launch { snackbarHostState.showSnackbar("✅ Izin Bluetooth diberikan") }
            } else {
                scope.launch { snackbarHostState.showSnackbar("❌ Izin Bluetooth ditolak") }
            }
        }
    )

    // Jalankan permission request saat screen dibuka
    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pilih Perangkat Bluetooth", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1976D2))
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (bluetoothAdapter == null) {
                Text("⚠️ Perangkat ini tidak mendukung Bluetooth")
                return@Column
            }

            if (!bluetoothAdapter.isEnabled) {
                Button(
                    onClick = {
                        context.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                ) {
                    Text("Aktifkan Bluetooth", color = Color.White)
                }
                return@Column
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    val bondedDevices = bluetoothAdapter.bondedDevices
                    pairedDevices = bondedDevices.toList()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1))
            ) {
                Text("Tampilkan Perangkat Terpasang", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (pairedDevices.isEmpty()) {
                Text("Belum ada perangkat yang terhubung.")
            } else {
                LazyColumn {
                    items(pairedDevices) { device ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("🔗 Menghubungkan ke ${device.name}...")
                                        // di sini nanti kita tambahkan proses koneksi
                                    }
                                },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Nama: ${device.name ?: "Tidak diketahui"}", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                                Text("Alamat: ${device.address}")
                            }
                        }
                    }
                }
            }
        }
    }
}
