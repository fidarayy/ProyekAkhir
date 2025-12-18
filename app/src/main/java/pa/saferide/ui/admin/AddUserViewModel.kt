package pa.saferide.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Model data untuk user baru
data class AdminUser(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val role: String = "user",
    val connected: Boolean = false
)

class AddUserViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = Firebase.firestore

    fun addUser(
        user: AdminUser,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // 1. VALIDASI INPUT
                if (user.username.isBlank() || user.email.isBlank() || user.password.isBlank()) {
                    throw Exception("Semua field harus diisi")
                }

                if (user.password.length < 6) {
                    throw Exception("Password minimal 6 karakter")
                }

                println("🔄 Starting to add user: ${user.email}")

                // 2. BUAT USER DI FIREBASE AUTHENTICATION (INI YANG PENTING!)
                val authResult = auth.createUserWithEmailAndPassword(user.email, user.password).await()
                val uid = authResult.user?.uid ?: throw Exception("Gagal membuat user di Firebase Auth")

                println("✅ User created in Firebase Auth. UID: $uid")

                // 3. SIMPAN DATA KE FIRESTORE
                val userData = hashMapOf<String, Any>(
                    "uid" to uid,
                    "username" to user.username,
                    "email" to user.email,
                    "role" to user.role,
                    "connected" to user.connected,
                    "createdAt" to com.google.firebase.Timestamp.now()
                    // JANGAN simpan password di Firestore untuk security
                )

                db.collection("users").document(uid).set(userData).await()
                println("✅ User data saved to Firestore")

                // 4. LOG INFORMASI UNTUK ADMIN
                println("""
                ========================================
                🎉 USER BERHASIL DITAMBAHKAN!
                ========================================
                🔐 INFORMASI LOGIN UNTUK USER:
                Email: ${user.email}
                Password: ${user.password}
                Username: ${user.username}
                Role: ${user.role}
                ========================================
                ⚠️ BERIKAN INFORMASI INI KE USER!
                ========================================
                """.trimIndent())

                onSuccess()

            } catch (e: Exception) {
                println("❌ Error adding user: ${e.message}")

                val errorMessage = when {
                    e.message?.contains("email-already-in-use") == true ->
                        Exception("Email sudah digunakan")
                    e.message?.contains("invalid-email") == true ->
                        Exception("Format email tidak valid")
                    e.message?.contains("weak-password") == true ->
                        Exception("Password terlalu lemah (minimal 6 karakter)")
                    else -> Exception("Gagal menambahkan user: ${e.message}")
                }

                onError(errorMessage)
            }
        }
    }
}