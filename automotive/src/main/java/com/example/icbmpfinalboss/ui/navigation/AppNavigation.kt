package com.example.icbmpfinalboss.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.icbmpfinalboss.ui.screens.fleet.FleetScreen
import com.example.icbmpfinalboss.ui.screens.map.MapViewScreen
import com.example.icbmpfinalboss.ui.screens.ota.OtaUpdateScreen
import com.example.icbmpfinalboss.ui.screens.overview.OverviewScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Overview.route // Default screen
    ) {
        composable(Screen.Overview.route) {
            OverviewScreen()
        }
        composable(Screen.FleetDetail.route) {
            FleetScreen()
        }
        composable(Screen.OtaUpdate.route) {
            OtaUpdateScreen()
        }
        composable(Screen.MapView.route) {
            MapViewScreen()
        }
    }
}
