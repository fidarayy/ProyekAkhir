package pa.saferide.ui.admin

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AdminNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "dashboard_admin"
    ) {
        composable("dashboard_admin") {
            DashboardAdminScreen(navController = navController)
        }
        composable("add_user") {
            AddUserScreen(navController = navController)
        }
        composable("admin_profile") {
            AdminProfileScreen(navController = navController)
        }
        composable("edit_admin") {
            EditAdminScreen(navController = navController)
        }
    }
}
