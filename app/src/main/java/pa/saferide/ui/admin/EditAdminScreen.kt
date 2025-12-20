package pa.saferide.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAdminScreen(navController: NavController) {

    val admin = FirebaseAuth.getInstance().currentUser

    // ⛔ ANTI CRASH
    if (admin == null) {
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
        return
    }

    var adminName by remember {
        mutableStateOf(
            admin.displayName ?: admin.email?.substringBefore("@") ?: ""
        )
    }

    var email by remember {
        mutableStateOf(admin.email ?: "")
    }

    var isSaving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Data Admin") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFE3F2FD), Color.White)
                    )
                )
                .padding(padding)
                .padding(24.dp)
        ) {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                OutlinedTextField(
                    value = adminName,
                    onValueChange = { adminName = it },
                    label = { Text("Nama Admin") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = {
                        isSaving = true
                        error = null

                        val request = userProfileChangeRequest {
                            displayName = adminName
                        }

                        admin.updateProfile(request)
                            .addOnSuccessListener {
                                if (email != admin.email) {
                                    admin.updateEmail(email)
                                        .addOnSuccessListener {
                                            isSaving = false
                                            navController.popBackStack()
                                        }
                                        .addOnFailureListener {
                                            isSaving = false
                                            error = "Email gagal diperbarui, login ulang diperlukan"
                                        }
                                } else {
                                    isSaving = false
                                    navController.popBackStack()
                                }
                            }
                            .addOnFailureListener {
                                isSaving = false
                                error = "Gagal menyimpan data"
                            }
                    },
                    enabled = !isSaving,
                    shape = CircleShape,
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Text("Simpan Perubahan")
                }

                error?.let {
                    Spacer(Modifier.height(12.dp))
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
