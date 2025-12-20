package pa.saferide.ui.screen

import ConnectionScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun UserNavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {

        // ================= DASHBOARD =================
        composable("dashboard") {
            DashboardScreen(navController = navController)
        }

        // ================= DEVICE =================
        composable("device") {
            DeviceScreen(navController = navController)
        }

        // ================= PROFILE =================
        composable("profile") {
            ProfileScreen(navController = navController)
        }

        // ================= CONNECTION =================
        composable("connection/{address}") { backStackEntry ->
            ConnectionScreen(
                deviceAddress = backStackEntry.arguments
                    ?.getString("address") ?: "",
                navController = navController
            )
        }
    }
}
