package pa.saferide.ui.screen

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {

    val context = LocalContext.current
    val prefs = context.getSharedPreferences("profile_prefs", Context.MODE_PRIVATE)

    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // ================= USER DATA =================
    val username = user?.displayName ?: user?.email?.substringBefore("@") ?: "User"
    val email = user?.email ?: ""

    // ================= PROFILE IMAGE (PERSISTENT) =================
    var profileImageUri by rememberSaveable {
        mutableStateOf(
            prefs.getString("profile_image_uri", null)?.let { Uri.parse(it) }
        )
    }

    val imagePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                profileImageUri = it
                prefs.edit().putString("profile_image_uri", it.toString()).apply()
            }
        }

    // ================= EDIT PROFILE =================
    var showEditDialog by remember { mutableStateOf(false) }
    var editUsername by remember { mutableStateOf(username) }

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(200)
        visible = true
    }

    // ================= SCAFFOLD =================
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },

        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {

                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate("dashboard") {
                            popUpTo("profile") { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Home") }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate("device") {
                            popUpTo("profile") { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color(0xFF4A6CFF), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Devices, null, tint = Color.White)
                        }
                    },
                    label = { Text("Device") }
                )

                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.Person, null) },
                    label = { Text("Profile") }
                )
            }
        },

        topBar = {
            TopAppBar(
                title = { Text("Profil Pengguna", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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
                    Brush.verticalGradient(listOf(Color(0xFFE3F2FD), Color.White))
                )
                .padding(padding)
        ) {

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600)) + slideInVertically { it / 3 },
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(Modifier.height(32.dp))

                    // ================= PROFILE IMAGE =================
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF90CAF9))
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (profileImageUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(profileImageUri),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                Icons.Default.Person,
                                null,
                                tint = Color.White,
                                modifier = Modifier.size(64.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Text(username, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text(email, fontSize = 14.sp, color = Color.Gray)

                    Spacer(Modifier.height(32.dp))

                    // ================= EDIT PHOTO BUTTON =================
                    OutlinedButton(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Image, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Edit Foto Profil")
                    }

                    Spacer(Modifier.height(12.dp))

                    // ================= EDIT PROFILE BUTTON =================
                    OutlinedButton(
                        onClick = { showEditDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Edit, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Edit Profile")
                    }
                }
            }
        }
    }

    // ================= EDIT PROFILE DIALOG =================
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Profile") },
            text = {
                OutlinedTextField(
                    value = editUsername,
                    onValueChange = { editUsername = it },
                    label = { Text("Username") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        try {
                            user?.updateProfile(
                                com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                    .setDisplayName(editUsername)
                                    .build()
                            )?.await()

                            snackbarHostState.showSnackbar("Profil berhasil diperbarui")
                            showEditDialog = false

                        } catch (e: Exception) {
                            snackbarHostState.showSnackbar(
                                e.message ?: "Gagal update profile"
                            )
                        }
                    }
                }) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}


