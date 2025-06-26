package com.example.icbmpfinalboss

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.icbmpfinalboss.ui.navigation.AppBottomNavigationBar
import com.example.icbmpfinalboss.ui.navigation.AppNavigation

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    val navController = rememberNavController() // Create the navigation controller[2]

    Scaffold(
        bottomBar = { AppBottomNavigationBar(navController = navController) }
    ) {
        // We need to pass the padding from the Scaffold to the NavHost
        // to prevent content from being drawn behind the navigation bar.
        // As we are not using it right now, we can ignore the warning
        // with the @SuppressLint annotation above.
        AppNavigation(navController = navController)
    }
}
