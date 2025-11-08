package pa.saferide.ui.screen

import ProfileViewModel
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val selectedImageUri by profileViewModel.profileImageUri
    var visible by remember { mutableStateOf(false) }

    // animasi muncul
    LaunchedEffect(Unit) {
        delay(200)
        visible = true
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        profileViewModel.updateProfileImage(uri)
    }

    // Menentukan halaman yang aktif untuk navigasi bawah
    val currentRoute = "profile"

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                // === HOME ===
                NavigationBarItem(
                    selected = currentRoute == "dashboard",
                    onClick = {
                        navController.navigate("dashboard") {
                            popUpTo("profile") { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = "Home",
                            tint = if (currentRoute == "dashboard") Color(0xFF4A6CFF) else Color.Black,
                            modifier = Modifier.size(28.dp)
                        )
                    },
                    label = {
                        Text(
                            "Home",
                            color = if (currentRoute == "dashboard") Color(0xFF4A6CFF) else Color.Black
                        )
                    }
                )

                // === DEVICE ===
                NavigationBarItem(
                    selected = currentRoute == "device",
                    onClick = {
                        navController.navigate("device") {
                            popUpTo("dashboard") { inclusive = false }
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
                            Icon(
                                Icons.Default.Devices,
                                contentDescription = "Device",
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    },
                    label = { Text("Device", color = Color.Black) }
                )

                // === PROFILE ===
                NavigationBarItem(
                    selected = currentRoute == "profile",
                    onClick = {
                        // Sudah di halaman profile, tidak perlu navigasi lagi
                    },
                    icon = {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Color.Black,
                            modifier = Modifier.size(28.dp)
                        )
                    },
                    label = {
                        Text("Profile", color = Color.Black
                        )
                    }
                )
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Profil Pengguna",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFE3F2FD), Color.White)
                    )
                )
                .padding(paddingValues)
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(700)) + slideInVertically(initialOffsetY = { it / 3 }),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Spacer(modifier = Modifier.height(32.dp))

                    var imgVisible by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) {
                        delay(300)
                        imgVisible = true
                    }

                    val scale by animateFloatAsState(
                        targetValue = if (imgVisible) 1f else 0.8f,
                        animationSpec = tween(600)
                    )

                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .scale(scale)
                            .clip(CircleShape)
                            .background(Color(0xFF90CAF9))
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedImageUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(selectedImageUri),
                                contentDescription = "Foto Profil",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Default Icon",
                                tint = Color.White,
                                modifier = Modifier.size(64.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E88E5)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Foto",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Fida Rahman",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "fida.rahman@example.com",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    Button(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1E88E5)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Ganti Foto",
                            tint = Color.White,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("Ganti Foto Profil", color = Color.White)
                    }
                }
            }
        }
    }
}
