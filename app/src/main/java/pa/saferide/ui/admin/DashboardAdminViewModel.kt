package pa.saferide.ui.admin

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DashboardAdminViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    val userList = mutableStateListOf<UserData>()

    fun fetchUsers() {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("users").get().await()
                userList.clear()

                for (doc in snapshot.documents) {
                    val username = doc.getString("username") ?: ""
                    val email = doc.getString("email") ?: ""
                    val connected = doc.getBoolean("connected") ?: false // ✅ aman

                    userList.add(UserData(username, email, connected))
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
