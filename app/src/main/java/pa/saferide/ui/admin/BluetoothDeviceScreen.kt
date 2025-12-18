package pa.saferide.ui.admin
import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BluetoothDeviceScreen(
    uid: String,
    navController: NavController,
    onDeviceSelected: (BluetoothDevice) -> Unit
) {
    val context = LocalContext.current
    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    var selectedDevice by remember { mutableStateOf<BluetoothDevice?>(null) }

    val hasBluetoothPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_GRANTED
            } else true
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Pilih Helmet") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            if (!hasBluetoothPermission) {
                Text(
                    "Izin Bluetooth belum diberikan",
                    modifier = Modifier.align(Alignment.Center)
                )
                return@Box
            }

            val bondedDevices = remember {
                try {
                    bluetoothAdapter?.bondedDevices?.toList() ?: emptyList()
                } catch (e: SecurityException) {
                    emptyList()
                }
            }

            if (bondedDevices.isEmpty()) {
                Text(
                    "Tidak ada helmet terpasang",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(bondedDevices) { device ->
                        BluetoothDeviceItem(
                            device = device,
                            onClick = {
                                selectedDevice = device
                            }
                        )
                    }
                }
            }

            // ================= ALERT DIALOG =================
            selectedDevice?.let { device ->
                AlertDialog(
                    onDismissRequest = { selectedDevice = null },
                    title = { Text("Hubungkan Helmet") },
                    text = {
                        Column {
                            Text("Nama: ${device.name ?: "Helmet ESP32"}")
                            Text("Alamat: ${device.address}")
                            Spacer(Modifier.height(8.dp))
                            Text("Hubungkan helmet ini ke user?")
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                onDeviceSelected(device)
                                selectedDevice = null
                            }
                        ) {
                            Text("Hubungkan")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { selectedDevice = null }) {
                            Text("Batal")
                        }
                    }
                )
            }
        }
    }
}


@Composable
private fun BluetoothDeviceItem(
    device: BluetoothDevice,
    onClick: () -> Unit
) {
    // ⚠️ GUARD WAJIB → LINT & RUNTIME AMAN
    val deviceName = remember {
        try {
            device.name ?: "Helmet ESP32"
        } catch (e: SecurityException) {
            "Helmet ESP32"
        }
    }

    val deviceAddress = remember {
        try {
            device.address
        } catch (e: SecurityException) {
            "Unknown"
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(deviceName, style = MaterialTheme.typography.titleMedium)
            Text(deviceAddress, style = MaterialTheme.typography.bodySmall)
        }
    }
}



