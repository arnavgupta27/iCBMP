package com.example.icbmpfinalboss

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.icbmpfinalboss.ui.navigation.AppNavigation // Your NavHost
import com.example.icbmpfinalboss.ui.navigation.AppNavigationRail // Our new Rail

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    val navController = rememberNavController()

    // The Scaffold no longer has a bottomBar
    Scaffold { innerPadding ->
        // We use a Row to lay out the screen content and the navigation rail horizontally
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Apply padding from the scaffold
        ) {
            // The main content area, which takes up all available space
            AppNavigation(
                navController = navController,
                modifier = Modifier.weight(1f) // This makes it fill the remaining space
            )

            // The Navigation Rail on the right side
            AppNavigationRail(navController = navController)
        }
    }
}
