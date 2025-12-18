package pa.saferide.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import pa.saferide.ui.main.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: MainViewModel = viewModel(),
    onLoginSuccessUser: () -> Unit = {},
    onLoginSuccessAdmin: () -> Unit = {},
    onNavigateToMigration: () -> Unit = {}
) {
    // PERBAIKAN: Ganti 'username' dengan 'email'
    val email by viewModel.email // ← PASTIKAN ViewModel punya property 'email'
    val password by viewModel.password
    val isLoading by viewModel.isLoading
    val loginSuccess by viewModel.loginSuccess
    val isAdmin by viewModel.isAdmin
    val errorMessage by viewModel.errorMessage

    var passwordVisible by remember { mutableStateOf(false) }
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        visible = true
    }

    LaunchedEffect(loginSuccess) {
        if (loginSuccess == true) {
            if (isAdmin) {
                onLoginSuccessAdmin()
            } else {
                onLoginSuccessUser()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFBBDEFB), Color(0xFFE3F2FD))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(800)),
            exit = fadeOut(tween(400))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(24.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White.copy(alpha = 0.9f))
                    .padding(24.dp)
            ) {

                var scaleAnim by remember { mutableStateOf(0.8f) }
                LaunchedEffect(Unit) {
                    delay(300)
                    scaleAnim = 1f
                }
                val scale by animateFloatAsState(
                    targetValue = scaleAnim,
                    animationSpec = tween(600)
                )

                Box(
                    modifier = Modifier
                        .scale(scale)
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Text("RS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                }

                Spacer(Modifier.height(12.dp))
                Text("RideSafe", fontSize = 26.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                Spacer(Modifier.height(32.dp))
                Text("Welcome Back", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("Login to continue your journey safely", color = Color.Gray, fontSize = 14.sp)
                Spacer(Modifier.height(36.dp))

                // PERBAIKAN 1: Ganti "Username" jadi "Email"
                OutlinedTextField(
                    value = email, // ← PAKAI email, BUKAN username
                    onValueChange = { viewModel.onEmailChange(it) }, // ← PAKAI onEmailChange
                    label = { Text("Email") }, // ← Label "Email"
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = icon, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(28.dp))

                Button(
                    onClick = { viewModel.login() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Login", color = Color.White, fontWeight = FontWeight.Medium)
                    }
                }

                // PERBAIKAN 2: Fix error message display
                if (loginSuccess == false || errorMessage != null) {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = errorMessage ?: "Email atau password salah",
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}