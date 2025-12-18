package pa.saferide.utils

import android.util.Log
import kotlin.random.Random
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.random.nextInt

object HelmetManager {
    private const val TAG = "HelmetManager"

    private var isEspConnected = false
    private var currentUserId: String? = null
    private var dataJob: Job? = null

    // Flow untuk data sensor real-time
    private val _sensorData = MutableStateFlow<Map<String, Any>?>(null)
    val sensorData: StateFlow<Map<String, Any>?> = _sensorData

    /**
     * Connect to ESP helmet
     */
    suspend fun connectToEsp(userId: String, username: String): Boolean {
        return withContext(Dispatchers.IO) {
            Log.d(TAG, "Connecting $username to ESP...")

            delay(2000) // Simulasi delay pairing

            val isSuccess = Random.nextBoolean() // 50% success rate untuk testing

            if (isSuccess) {
                isEspConnected = true
                currentUserId = userId
                startDataStream(username)
                Log.d(TAG, "✅ Connected: $username")
            } else {
                Log.d(TAG, "❌ Failed to connect: $username")
            }

            isSuccess
        }
    }

    /**
     * Disconnect from ESP
     */
    fun disconnectFromEsp() {
        isEspConnected = false
        currentUserId = null
        dataJob?.cancel()
        dataJob = null
        Log.d(TAG, "🔌 Disconnected from ESP")
    }

    /**
     * Start simulated data stream
     */
    private fun startDataStream(username: String) {
        dataJob?.cancel()

        dataJob = CoroutineScope(Dispatchers.Default).launch {
            var counter = 0
            while (isEspConnected) {
                // Generate simulated sensor data
                val data = mapOf(
                    "userId" to username,
                    "counter" to counter,
                    "accelX" to Random.nextDouble(-2.0, 2.0),
                    "accelY" to Random.nextDouble(-2.0, 2.0),
                    "accelZ" to Random.nextDouble(8.0, 12.0),
                    "gyroX" to Random.nextDouble(-180.0, 180.0),
                    "gyroY" to Random.nextDouble(-180.0, 180.0),
                    "gyroZ" to Random.nextDouble(-180.0, 180.0),
                    "speed" to Random.nextDouble(0.0, 40.0),
                    "hasImpact" to (Random.nextInt(0..100) > 98), // 2% impact chance
                    "timestamp" to System.currentTimeMillis()
                )

                _sensorData.value = data

                // Log data
                if (data["hasImpact"] == true) {
                    Log.w(TAG, "⚠️ IMPACT DETECTED for $username!")
                }

                counter++
                delay(1000) // Emit every 1 second
            }
        }
    }

    /**
     * Check connection status
     */
    fun isConnected(): Boolean = isEspConnected

    /**
     * Get connected user
     */
    fun getConnectedUser(): String? = currentUserId
}