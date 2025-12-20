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

class MainViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = Firebase.firestore

    // ================= STATE =================
    val email = mutableStateOf("")
    val password = mutableStateOf("")
    val isLoading = mutableStateOf(false)

    // 🔥 NON NULL SEMUA
    val loginSuccess = mutableStateOf(false)
    val isAdmin = mutableStateOf(false)

    val errorMessage = mutableStateOf<String?>(null)

    // ================= LOGIN =================
    fun login() {
        if (email.value.isBlank() || password.value.isBlank()) {
            errorMessage.value = "Email dan password harus diisi"
            return
        }

        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            loginSuccess.value = false
            isAdmin.value = false

            try {
                // 1. Firebase Auth
                val authResult = auth
                    .signInWithEmailAndPassword(email.value, password.value)
                    .await()

                val uid = authResult.user?.uid
                    ?: throw Exception("User tidak ditemukan")

                // 2. Ambil role dari Firestore
                val userDoc = db.collection("users").document(uid).get().await()
                if (!userDoc.exists()) {
                    throw Exception("Data user tidak ditemukan")
                }

                val role = userDoc.getString("role") ?: "user"

                // 3. Set role
                isAdmin.value = role == "admin"

                // 4. Login sukses
                loginSuccess.value = true

            } catch (e: Exception) {
                loginSuccess.value = false
                errorMessage.value = getErrorMessage(e)
            } finally {
                isLoading.value = false
            }
        }
    }

    // ================= LOGOUT =================
    fun logout() {
        auth.signOut()
        reset()
    }

    // ================= RESET =================
    private fun reset() {
        email.value = ""
        password.value = ""
        isLoading.value = false
        loginSuccess.value = false
        isAdmin.value = false
        errorMessage.value = null
    }

    // ================= INPUT =================
    fun onEmailChange(newEmail: String) {
        email.value = newEmail
        errorMessage.value = null
    }

    fun onPasswordChange(newPassword: String) {
        password.value = newPassword
        errorMessage.value = null
    }

    private fun getErrorMessage(e: Exception): String {
        return when {
            e.message?.contains("password", true) == true ->
                "Password salah"
            e.message?.contains("user", true) == true ->
                "Email tidak terdaftar"
            e.message?.contains("network", true) == true ->
                "Koneksi internet bermasalah"
            else ->
                "Login gagal"
        }
    }
}
