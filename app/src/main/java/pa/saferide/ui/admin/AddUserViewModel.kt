package pa.saferide.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Model data untuk user baru
data class AdminUser(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val role: String = "user",          // ✅ tambahkan field agar jelas peran user
    val connected: Boolean = false      // ✅ supaya tidak error di DashboardAdminScreen
)

class AddUserViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    fun addUser(
        user: AdminUser,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                firestore.collection("users")
                    .add(user)
                    .await()
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
}
