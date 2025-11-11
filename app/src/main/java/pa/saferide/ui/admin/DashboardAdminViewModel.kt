package pa.saferide.ui.admin

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DashboardAdminViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    // state list yang bisa dipakai di Compose
    val userList = mutableStateListOf<UserData>()

    // fetch semua user dari koleksi "users"
    fun fetchUsers() {
        viewModelScope.launch {
            try {
                val snapshot = firestore.collection("users").get().await()
                userList.clear()
                for (doc in snapshot.documents) {
                    // asumsikan dokumen sesuai struktur AdminUser
                    val username = doc.getString("username") ?: ""
                    val email = doc.getString("email") ?: ""
                    // jika kamu menyimpan status connection, ambil juga, mis: "connected" boolean
                    val connected = doc.getBoolean("connected") ?: false
                    userList.add(UserData(username, email, connected))
                }
            } catch (e: Exception) {
                // logging bila perlu
                e.printStackTrace()
            }
        }
    }

    /**
     * Hapus semua dokumen yang memiliki field "username" == username
     * (biasanya username unik; kalau pakai uid pakai field uid lebih aman)
     */
    fun deleteUser(
        username: String,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                // cari dokumen user berdasarkan username
                val querySnapshot = firestore.collection("users")
                    .whereEqualTo("username", username)
                    .get()
                    .await()

                // jika tidak ada doc, tetap panggil success (atau handle sesuai kebutuhan)
                if (querySnapshot.isEmpty) {
                    onSuccess()
                    // refresh list
                    fetchUsers()
                    return@launch
                }

                // hapus semua dokumen hasil query
                for (doc in querySnapshot.documents) {
                    firestore.collection("users").document(doc.id).delete().await()
                }

                // refresh list setelah hapus
                fetchUsers()

                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
}
