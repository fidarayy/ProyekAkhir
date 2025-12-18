package pa.saferide.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import pa.saferide.ui.admin.AdminNavGraph
import pa.saferide.ui.admin.BluetoothDeviceScreen
import pa.saferide.ui.admin.DetailUserScreen
import pa.saferide.ui.screen.LoginScreen
import pa.saferide.ui.screen.UserNavGraph

@Composable
fun NavGraph(rootNavController: NavHostController) {
    NavHost(
        navController = rootNavController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccessUser = {
                    rootNavController.navigate("user_app") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onLoginSuccessAdmin = {
                    rootNavController.navigate("admin_app") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // NAVHOST untuk USER
        composable("user_app") {
            val userNavController = rememberNavController()
            UserNavGraph(navController = userNavController)
        }

        // NAVHOST untuk ADMIN
        composable("admin_app") {
            val adminNavController = rememberNavController()
            AdminNavGraph(navController = adminNavController)
        }
    }
}