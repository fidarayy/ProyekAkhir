package pa.saferide.ui.main

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pa.saferide.ui.admin.DashboardAdminScreen
import pa.saferide.ui.screen.DashboardScreen
import pa.saferide.ui.screen.DeviceScreen
import pa.saferide.ui.screen.LoginScreen
import pa.saferide.ui.screen.ProfileScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            // 🧠 LoginScreen sekarang mendukung dua callback:
            LoginScreen(
                onLoginSuccessUser = {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onLoginSuccessAdmin = {
                    navController.navigate("dashboard_admin") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("dashboard") {
            DashboardScreen(navController)
        }

        composable("profile") {
            ProfileScreen(navController = navController)
        }

        composable("device") {
            DeviceScreen(navController = navController)
        }

        composable("dashboard_admin") {
            DashboardAdminScreen(navController = navController)
        }
    }
}