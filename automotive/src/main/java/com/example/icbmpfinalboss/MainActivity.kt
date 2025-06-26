package com.example.icbmpfinalboss

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.icbmpfinalboss.ui.theme.CloudBMSDashboardTheme

import com.mapbox.maps.*
import com.mapbox.maps.plugin.Plugin.Mapbox

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CloudBMSDashboardTheme {
                MainScreen()
            }
        }
    }
}
