package pa.saferide.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import pa.saferide.ui.admin.DashboardAdminScreen
import pa.saferide.ui.screen.DashboardScreen
import pa.saferide.ui.screen.DeviceScreen
import pa.saferide.ui.screen.LoginScreen
import pa.saferide.ui.screen.ProfileScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
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
            DashboardScreen(navController = navController)
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