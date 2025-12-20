package pa.saferide.bluetooth

import android.bluetooth.*
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class BluetoothManager {

    private var socket: BluetoothSocket? = null

    suspend fun connect(device: BluetoothDevice): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val uuid =
                    device.uuids?.firstOrNull()?.uuid
                        ?: UUID.fromString("00001101-0000-1000-8000-00805F9B34")

                socket = device.createRfcommSocketToServiceRecord(uuid)
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                socket?.connect()
                true
            } catch (e: Exception) {
                Log.e("BT", "Connect error", e)
                false
            }
        }

    suspend fun sendWifi(ssid: String, password: String): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val json =
                    """{"ssid":"$ssid","password":"$password"}"""

                socket?.outputStream?.apply {
                    write(json.toByteArray())
                    flush()
                }
                true
            } catch (e: Exception) {
                Log.e("BT", "Send error", e)
                false
            }
        }

    fun close() {
        socket?.close()
        socket = null
    }
}
