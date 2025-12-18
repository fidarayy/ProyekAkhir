package pa.saferide.ui.model

import com.google.firebase.Timestamp

data class UserData(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val role: String = "user",
    val helmetId: String? = null,
    val connected: Boolean = false,
    val createdAt: Timestamp? = null
)
