package pa.saferide.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import pa.saferide.ui.screen.DashboardScreen
import pa.saferide.ui.screen.LoginScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(onLoginSuccess = {
                navController.navigate("dashboard") {
                    popUpTo("login") { inclusive = true } // hapus login dari backstack
                }
            })
        }
        composable("dashboard") {
            DashboardScreen()
        }
    }
}