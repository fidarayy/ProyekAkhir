package pa.saferide.ui.admin

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import pa.saferide.ui.model.UserData

class DashboardAdminViewModel : ViewModel() {

    val users = mutableStateOf<List<UserData>>(emptyList())
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)

    private val db: FirebaseFirestore = Firebase.firestore

    // ================= LOAD USERS =================
    fun loadUsers() {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            try {
                val snapshot = db.collection("users").get().await()

                val list = snapshot.documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null

                    val createdAt = when (val raw = data["createdAt"]) {
                        is Timestamp -> raw.toDate().time
                        is Long -> raw
                        is Double -> raw.toLong()
                        else -> 0L
                    }

                    UserData(
                        uid = doc.id,
                        username = data["username"] as? String ?: "",
                        email = data["email"] as? String ?: "",
                        role = data["role"] as? String ?: "user",
                        helmetId = data["helmetId"] as? String ?: "",
                        connected = data["connected"] as? Boolean ?: false,
                        createdAt = createdAt
                    )
                }

                users.value = list.sortedByDescending { it.createdAt }

            } catch (e: Exception) {
                errorMessage.value = "Gagal memuat data user"
            } finally {
                isLoading.value = false
            }
        }
    }

    // ================= UPDATE USER (FIRESTORE ONLY) =================
//    fun updateUserProfile(
//        uid: String,
//        username: String,
//        email: String,
//        onSuccess: () -> Unit,
//        onError: (String) -> Unit
//    ) {
//        viewModelScope.launch {
//            isLoading.value = true
//            errorMessage.value = null
//
//            try {
//                db.collection("users")
//                    .document(uid)
//                    .update(
//                        mapOf(
//                            "username" to username,
//                            "email" to email
//                        )
//                    )
//                    .await()
//
//                users.value = users.value.map {
//                    if (it.uid == uid) {
//                        it.copy(username = username, email = email)
//                    } else it
//                }
//
//                onSuccess()
//
//            } catch (e: Exception) {
//                onError(e.message ?: "Gagal memperbarui data user")
//            } finally {
//                isLoading.value = false
//            }
//        }
//    }

    // ================= UPDATE CONNECTION =================
    fun updateUserConnection(uid: String, connected: Boolean) {
        viewModelScope.launch {
            try {
                db.collection("users")
                    .document(uid)
                    .update("connected", connected)
                    .await()

                users.value = users.value.map {
                    if (it.uid == uid) it.copy(connected = connected)
                    else it
                }
            } catch (e: Exception) {
                errorMessage.value = "Gagal update koneksi"
            }
        }
    }

    // ================= DELETE USER =================
    fun deleteUser(uid: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                db.collection("users")
                    .document(uid)
                    .delete()
                    .await()

                users.value = users.value.filterNot { it.uid == uid }

            } catch (e: Exception) {
                errorMessage.value = "Gagal menghapus user"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun clearError() {
        errorMessage.value = null
    }
}
