package pa.saferide.ui.main

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainViewModel : ViewModel() {

    // ======= State yang dipakai di UI =======
    var username = mutableStateOf("")
        private set

    var password = mutableStateOf("")
        private set

    var isLoading = mutableStateOf(false)
        private set

    var loginSuccess = mutableStateOf<Boolean?>(null)
        private set

    // ======= Fungsi untuk mengubah nilai =======
    fun onUsernameChange(newUsername: String) {
        username.value = newUsername
    }

    fun onPasswordChange(newPassword: String) {
        password.value = newPassword
    }

    // ======= Logika Login =======
    fun login() {
        viewModelScope.launch {
            isLoading.value = true
            delay(1000) // efek loading agar terlihat realistis

            // Validasi login sederhana
            if (username.value == "fida" && password.value == "123456789") {
                loginSuccess.value = true
            } else {
                loginSuccess.value = false
            }

            isLoading.value = false
        }
    }
}