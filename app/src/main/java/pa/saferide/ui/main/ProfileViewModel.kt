import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {
    // Menyimpan foto profil sementara
    var profileImageUri = mutableStateOf<Uri?>(null)

    fun updateProfileImage(uri: Uri?) {
        profileImageUri.value = uri
    }
}