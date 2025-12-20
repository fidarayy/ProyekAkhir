package pa.saferide.ui.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserScreen(
    navController: NavController,
    viewModel: AddUserViewModel = viewModel()
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val gradientBackground = Brush.verticalGradient(
        colors = listOf(Color(0xFF1976D2), Color(0xFFE3F2FD))
    )

    var contentVisible by remember { mutableStateOf(false) }
    val alphaAnim by animateFloatAsState(
        targetValue = if (contentVisible) 1f else 0f,
        animationSpec = tween(800),
        label = ""
    )

    LaunchedEffect(Unit) {
        delay(200)
        contentVisible = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Tambah Pengguna Baru",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1565C0)
                )
            )
        },

        bottomBar = {
            NavigationBar(containerColor = Color.White) {

                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate("dashboard_admin") {
                            popUpTo("add_user") { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Home") }
                )

                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0xFF1976D2))
                                .padding(8.dp)
                        ) {
                            Icon(Icons.Default.Add, null, tint = Color.White)
                        }
                    },
                    label = { Text("Add User") }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("admin_profile") },
                    icon = { Icon(Icons.Default.Person, null) },
                    label = { Text("Profile") }
                )
            }
        },

        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBackground)
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .alpha(alphaAnim)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White.copy(alpha = 0.95f))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    "Isi data pengguna baru",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0D47A1)
                )

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(12.dp))

                // 🔐 PASSWORD FIELD (FIXED)
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),

                    visualTransformation = if (showPassword)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation()
                )

                Spacer(Modifier.height(6.dp))

                // ☑️ CHECKBOX SHOW PASSWORD
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = showPassword,
                        onCheckedChange = { showPassword = it }
                    )
                    Text("Tampilkan Password")
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (username.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {

                            val newUser = AdminUser(
                                username = username,
                                email = email,
                                password = password,
                                role = "user",
                                connected = false
                            )

                            viewModel.addUser(
                                user = newUser,
                                onSuccess = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("✅ Pengguna berhasil ditambahkan")
                                        delay(600)
                                        navController.navigate("dashboard_admin") {
                                            popUpTo("add_user") { inclusive = true }
                                        }
                                    }
                                },
                                onError = { e ->
                                    scope.launch {
                                        snackbarHostState.showSnackbar("❌ ${e.message}")
                                    }
                                }
                            )

                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("⚠ Semua field wajib diisi")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1565C0),
                        contentColor = Color.White
                    )
                ) {
                    Text("Tambah Pengguna", fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(12.dp))

                AnimatedVisibility(
                    visible = username.isNotBlank() || email.isNotBlank() || password.isNotBlank()
                ) {
                    Text(
                        "Pastikan data sudah benar sebelum menyimpan.",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
