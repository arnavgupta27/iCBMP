package com.example.icbmpfinalboss.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Overview : Screen(
        route = "overview",
        title = "Overview",
        icon = Icons.Default.Home
    )
    object FleetDetail : Screen(
        route = "fleet",
        title = "Fleet",
        icon = Icons.Default.List
    )
    object OtaUpdate : Screen(
        route = "ota",
        title = "OTA",
        icon = Icons.Default.Settings
    )
    object MapView : Screen(
        route = "map",
        title = "Map",
        icon = Icons.Default.LocationOn
    )
}
