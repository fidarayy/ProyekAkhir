package pa.saferide.ui.admin

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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

            // ===== CONFIRMATION DIALOG =====
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
                        TextButton(onClick = {
                            onDeviceSelected(device)
                            selectedDevice = null
                            navController.navigateUp()
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

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun BluetoothDeviceScreen(
//    uid: String,
//    navController: NavController,
//    onDeviceSelected: (BluetoothDevice) -> Unit
//) {
//    val context = LocalContext.current
//    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
//
//    var selectedDevice by remember { mutableStateOf<BluetoothDevice?>(null) }
//    val discoveredDevices = remember { mutableStateListOf<BluetoothDevice>() }
//
//    // ================= PERMISSION =================
//    val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//        arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
//    } else emptyArray()
//
//    var allPermissionsGranted by remember {
//        mutableStateOf(requiredPermissions.all {
//            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
//        })
//    }
//
//    val permissionsLauncher = rememberLauncherForActivityResult(
//        ActivityResultContracts.RequestMultiplePermissions()
//    ) { perms ->
//        allPermissionsGranted = perms.values.all { it }
//    }
//
//    LaunchedEffect(Unit) {
//        if (!allPermissionsGranted && requiredPermissions.isNotEmpty()) {
//            permissionsLauncher.launch(requiredPermissions)
//        }
//    }
//
//    // ================= BLUETOOTH CHECK =================
//    LaunchedEffect(Unit) {
//        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled) {
//            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//            context.startActivity(enableIntent)
//        }
//    }
//
//    // ================= DISCOVERY =================
//    DisposableEffect(allPermissionsGranted) {
//        if (!allPermissionsGranted || bluetoothAdapter == null) {
//            return@DisposableEffect onDispose { }
//        }
//
//        if (bluetoothAdapter.isDiscovering) bluetoothAdapter.cancelDiscovery()
//        bluetoothAdapter.startDiscovery()
//
//        val receiver = object : BroadcastReceiver() {
//            override fun onReceive(context: Context?, intent: Intent?) {
//                if (intent?.action == BluetoothDevice.ACTION_FOUND) {
//                    val device =
//                        intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
//                    device?.let {
//                        if (discoveredDevices.none { it.address == device.address }) {
//                            discoveredDevices.add(it)
//                        }
//                    }
//                }
//            }
//        }
//
//        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
//        context.registerReceiver(receiver, filter)
//
//        onDispose {
//            bluetoothAdapter.cancelDiscovery()
//            context.unregisterReceiver(receiver)
//        }
//    }
//
//    // ================= UI =================
//    Scaffold(
//        topBar = {
//            CenterAlignedTopAppBar(
//                title = { Text("Pilih Helmet") },
//                navigationIcon = {
//                    IconButton(onClick = { navController.navigateUp() }) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = null)
//                    }
//                }
//            )
//        }
//    ) { padding ->
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(padding)
//        ) {
//
//            if (!allPermissionsGranted) {
//                Column(
//                    modifier = Modifier.align(Alignment.Center),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text("Izin Bluetooth belum diberikan")
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Button(onClick = { permissionsLauncher.launch(requiredPermissions) }) {
//                        Text("Minta Izin")
//                    }
//                }
//                return@Box
//            }
//
//            if (discoveredDevices.isEmpty()) {
//                Text(
//                    "Mencari helmet di sekitar...",
//                    modifier = Modifier.align(Alignment.Center)
//                )
//            } else {
//                LazyColumn(
//                    modifier = Modifier.fillMaxSize(),
//                    contentPadding = PaddingValues(16.dp),
//                    verticalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    items(discoveredDevices) { device ->
//                        BluetoothDeviceItem(
//                            device = device,
//                            onClick = { selectedDevice = device }
//                        )
//                    }
//                }
//            }
//
//            // ================= ALERT DIALOG =================
//            selectedDevice?.let { device ->
//                AlertDialog(
//                    onDismissRequest = { selectedDevice = null },
//                    title = { Text("Hubungkan Helmet") },
//                    text = {
//                        Column {
//                            Text("Nama: ${device.name ?: "Helmet ESP32"}")
//                            Text("Alamat: ${device.address}")
//                            Spacer(Modifier.height(8.dp))
//                            Text("Hubungkan helmet ini?")
//                        }
//                    },
//                    confirmButton = {
//                        TextButton(
//                            onClick = {
//                                onDeviceSelected(device)
//                                selectedDevice = null
//                            }
//                        ) { Text("Hubungkan") }
//                    },
//                    dismissButton = {
//                        TextButton(onClick = { selectedDevice = null }) { Text("Batal") }
//                    }
//                )
//            }
//        }
//    }
//}

@Composable
private fun BluetoothDeviceItem(
    device: BluetoothDevice,
    onClick: () -> Unit
) {
    val deviceName = remember {
        try { device.name ?: "Helmet ESP32" } catch (e: SecurityException) { "Helmet ESP32" }
    }

    val deviceAddress = remember {
        try { device.address } catch (e: SecurityException) { "Unknown" }
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

