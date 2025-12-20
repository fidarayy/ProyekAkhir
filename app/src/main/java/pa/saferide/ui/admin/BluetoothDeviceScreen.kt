package pa.saferide.ui.admin

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.withContext
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BluetoothDeviceScreen(
    uid: String,
    navController: NavController
) {
    val context = LocalContext.current
    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    val scope = rememberCoroutineScope()
    val bluetoothManager = remember {
        BluetoothManagerHelper(context)
    }
    var selectedDevice by remember { mutableStateOf<BluetoothDevice?>(null) }

    val hasPermission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else true

    val bondedDevices = remember(hasPermission) {
        if (!hasPermission || bluetoothAdapter == null) emptyList()
        else {
            try {
                bluetoothAdapter.bondedDevices.toList()
            } catch (e: SecurityException) {
                emptyList()
            }
        }
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

            if (!hasPermission) {
                Text(
                    "Izin Bluetooth belum diberikan",
                    modifier = Modifier.align(Alignment.Center)
                )
                return@Box
            }

            if (bondedDevices.isEmpty()) {
                Text(
                    "Tidak ada helmet terpasang.\nPair SmartHelm di Pengaturan Bluetooth.",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(bondedDevices) { device ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedDevice = device }
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(
                                    device.name ?: "Helmet ESP32",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    device.address,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            // ===== DIALOG KONFIRMASI =====
            selectedDevice?.let { device ->
                AlertDialog(
                    onDismissRequest = { selectedDevice = null },
                    title = { Text("Hubungkan Helmet") },
                    text = {
                        Column {
                            Text("Nama: ${device.name ?: "Helmet ESP32"}")
                            Text("Alamat: ${device.address}")
                            Spacer(Modifier.height(8.dp))
                            Text("Kirim UID ke helmet?")
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {

                            scope.launch {
                                val result = bluetoothManager.connectAndSendPath(
                                    device = device,
                                    uid = uid
                                )

                                if (result.isSuccess) {
                                    Toast.makeText(
                                        context,
                                        "Berhasil mengirim data $uid ke helmet",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    navController.navigateUp()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Gagal mengirim data ke helmet",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            selectedDevice = null

                        }) {
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
