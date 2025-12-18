package pa.saferide.ui.main

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import pa.saferide.ui.model.UserData

class MainViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = Firebase.firestore

    // State
    val email = mutableStateOf("")
    val password = mutableStateOf("")
    val isLoading = mutableStateOf(false)
    val loginSuccess = mutableStateOf<Boolean?>(null)
    val isAdmin = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)

    // Admin credentials (hardcoded)
    private val ADMIN_EMAIL = "admin@saferide.com"
    private val ADMIN_PASSWORD = "admin123" // Ganti dengan password kuat

    init {
        // Cek dan buat admin saat aplikasi pertama kali dijalankan
        checkAndCreateAdmin()
    }

    // ======= AUTO-CREATE ADMIN =======
    private fun checkAndCreateAdmin() {
        viewModelScope.launch {
            try {
                println("🔍 Checking admin account...")

                // Coba login dengan admin credentials
                val authResult = auth.signInWithEmailAndPassword(ADMIN_EMAIL, ADMIN_PASSWORD).await()
                val uid = authResult.user?.uid ?: return@launch

                println("✅ Admin exists in Firebase Auth")

                // Cek apakah admin sudah ada di Firestore
                val adminDoc = db.collection("users").document(uid).get().await()

                if (!adminDoc.exists()) {
                    // Buat data admin di Firestore
                    val adminData = hashMapOf<String, Any>(
                        "username" to "Admin",
                        "email" to ADMIN_EMAIL,
                        "role" to "admin",
                        "connected" to false,
                        "createdAt" to com.google.firebase.Timestamp.now()
                    )

                    db.collection("users").document(uid).set(adminData).await()
                    println("✅ Admin data created in Firestore")
                } else {
                    println("✅ Admin already exists in Firestore")
                }

                // Sign out setelah setup
                auth.signOut()
                println("👋 Signed out after admin setup")

            } catch (e: Exception) {
                if (e.message?.contains("invalid credential") == true ||
                    e.message?.contains("user-not-found") == true) {
                    // Admin belum ada, buat baru
                    createAdminAccount()
                } else {
                    println("⚠️ Error checking admin: ${e.message}")
                }
            }
        }
    }

    private suspend fun createAdminAccount() {
        try {
            println("🔄 Creating admin account...")

            // Buat user di Firebase Auth
            val authResult = auth.createUserWithEmailAndPassword(ADMIN_EMAIL, ADMIN_PASSWORD).await()
            val uid = authResult.user?.uid ?: throw Exception("Failed to create admin")

            println("✅ Admin created in Firebase Auth: $uid")

            // Buat data di Firestore
            val adminData = hashMapOf<String, Any>(
                "username" to "Admin",
                "email" to ADMIN_EMAIL,
                "role" to "admin",
                "connected" to false,
                "createdAt" to com.google.firebase.Timestamp.now()
            )

            db.collection("users").document(uid).set(adminData).await()
            println("✅ Admin data saved to Firestore")

            // Sign out setelah create
            auth.signOut()
            println("👋 Signed out after admin creation")

        } catch (e: Exception) {
            println("❌ Failed to create admin: ${e.message}")
        }
    }

    // ======= LOGIN FUNCTION =======
    fun login() {
        // Validasi
        if (email.value.isBlank() || password.value.isBlank()) {
            errorMessage.value = "Email dan password harus diisi"
            loginSuccess.value = false
            return
        }

        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            loginSuccess.value = null

            try {
                println("🔐 Attempting login: ${email.value}")

                // 1. Login dengan Firebase Auth
                val authResult = auth.signInWithEmailAndPassword(email.value, password.value).await()
                val firebaseUser = authResult.user ?: throw Exception("User not found")
                val uid = firebaseUser.uid

                println("✅ Firebase Auth success. UID: $uid")

                // 2. Ambil data dari Firestore
                val userDoc = db.collection("users").document(uid).get().await()

                if (userDoc.exists()) {
                    val data = userDoc.data ?: mapOf()
                    val role = data["role"] as? String ?: "user"

                    println("📊 User data: Role=$role, Email=${data["email"]}")

                    // 3. Update state
                    isAdmin.value = role == "admin"
                    loginSuccess.value = true
                    errorMessage.value = null

                    if (isAdmin.value) {
                        println("🎉 ADMIN LOGIN SUCCESSFUL")
                    } else {
                        println("👤 USER LOGIN SUCCESSFUL")
                    }

                } else {
                    // User ada di Auth tapi tidak di Firestore
                    errorMessage.value = "Data user tidak ditemukan"
                    loginSuccess.value = false
                    println("❌ User not found in Firestore")
                }

            } catch (e: Exception) {
                loginSuccess.value = false
                errorMessage.value = getErrorMessage(e)
                println("❌ Login error: ${e.javaClass.simpleName} - ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }

    private fun getErrorMessage(e: Exception): String {
        return when {
            e.message?.contains("invalid credential") == true -> "Password salah"
            e.message?.contains("user-not-found") == true -> "Email tidak terdaftar"
            e.message?.contains("network error") == true -> "Koneksi internet bermasalah"
            e.message?.contains("badly formatted") == true -> "Format email tidak valid"
            else -> "Login gagal: ${e.localizedMessage}"
        }
    }

    // ======= HELPER FUNCTIONS =======
    fun onEmailChange(newEmail: String) {
        email.value = newEmail
        clearError()
    }

    fun onPasswordChange(newPassword: String) {
        password.value = newPassword
        clearError()
    }

    fun clearError() {
        errorMessage.value = null
        loginSuccess.value = null
    }

    fun reset() {
        email.value = ""
        password.value = ""
        isLoading.value = false
        loginSuccess.value = null
        isAdmin.value = false
        errorMessage.value = null
    }

    fun logout() {
        auth.signOut()
        reset()
        println("👋 User logged out")
    }

    // ======= ADD USER (Admin Function) =======
    fun addUser(
        username: String,
        email: String,
        password: String,
        role: String = "user",
        helmetId: String? = null,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            isLoading.value = true

            try {
                println("🔄 Adding new user: $email")

                // 1. Buat user di Firebase Auth
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val uid = authResult.user?.uid ?: throw Exception("Failed to create user")

                println("✅ User created in Auth: $uid")

                // 2. Simpan data ke Firestore
                val userData = hashMapOf<String, Any>(
                    "uid" to uid,
                    "username" to username,
                    "email" to email,
                    "role" to role,
                    "connected" to false,
                    "createdAt" to com.google.firebase.Timestamp.now()
                )

                helmetId?.let { userData["helmetId"] = it }

                db.collection("users").document(uid).set(userData).await()
                println("✅ User data saved to Firestore")

                onSuccess()

            } catch (e: Exception) {
                val errorMsg = when {
                    e.message?.contains("email-already-in-use") == true -> "Email sudah digunakan"
                    e.message?.contains("password") == true -> "Password minimal 6 karakter"
                    else -> "Gagal membuat user: ${e.localizedMessage}"
                }
                onError(errorMsg)
                println("❌ Error adding user: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }
}