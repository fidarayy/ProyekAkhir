package pa.saferide.ui.admin

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.UUID

class BluetoothManagerHelper(context: Context) {

    private val ESP32_UUID =
        UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // SPP

    suspend fun connectAndSendPath(
        device: BluetoothDevice,
        uid: String
    ): Result<Unit> {

        return withContext(Dispatchers.IO) {
            try {
                val socket: BluetoothSocket =
                    device.createRfcommSocketToServiceRecord(ESP32_UUID)

                socket.connect()

                val json = """{"path": "users/$uid/sensor"}\n"""
                socket.outputStream.write(json.toByteArray())
                socket.outputStream.flush()

                // beri waktu ESP32 baca data
                delay(300)

                socket.close()

                Result.success(Unit)

            } catch (e: Exception) {
                Log.e("BT_MANAGER", "Gagal kirim data", e)
                Result.failure(e)
            }
        }
    }
}
