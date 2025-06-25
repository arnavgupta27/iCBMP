package com.example.icbmpfinalboss

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme // Correct Material 3 import
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.icbmpfinalboss.ui.DashboardScreen // Import your screen
import com.example.icbmpfinalboss.ui.theme.CloudBMSDashboardTheme // Import your theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // --- THE FIX IS HERE ---
            // By wrapping everything in your theme, you provide the colorScheme
            // and other theme attributes to all child composables.
            CloudBMSDashboardTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background // Now this will work
                ) {
                    // DashboardScreen is now a descendant of the theme and can access its colors.
                    DashboardScreen()
                }
            }
        }
    }
}
