package pa.saferide.ui.admin

import android.bluetooth.BluetoothDevice

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

@Composable
fun AdminNavGraph(navController: NavHostController) {

    val dashboardViewModel: DashboardAdminViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "dashboard_admin"
    ) {

        composable("dashboard_admin") {
            DashboardAdminScreen(
                navController = navController,
                viewModel = dashboardViewModel
            )
        }

        composable("add_user") {
            AddUserScreen(navController)
        }

        composable("admin_profile") {
            AdminProfileScreen(navController)
        }

        // ✅ TAMBAHKAN INI
        composable("edit_admin") {
            EditAdminScreen(navController)
        }

        composable(
            route = "user_detail/{uid}",
            arguments = listOf(
                navArgument("uid") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val uid = backStackEntry.arguments?.getString("uid") ?: return@composable

            DetailUserScreen(
                uid = uid,
                navController = navController,
                viewModel = dashboardViewModel
            )
        }

        composable(
            route = "bluetooth_device/{uid}",
            arguments = listOf(
                navArgument("uid") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val uid = backStackEntry.arguments?.getString("uid") ?: return@composable

            BluetoothDeviceScreen(
                uid = uid,
                navController = navController
            )
        }

        composable(
            route = "edit_user/{uid}",
            arguments = listOf(
                navArgument("uid") { type = NavType.StringType }
            )
        ) {
            DashboardAdminScreen(navController, dashboardViewModel)
        }
    }
}

