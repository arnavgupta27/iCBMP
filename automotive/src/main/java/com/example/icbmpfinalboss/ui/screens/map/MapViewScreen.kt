package com.example.icbmpfinalboss.ui.screens.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mapbox.maps.extension.compose.MapboxMap

@Composable
fun MapViewScreen() {
    MapboxMap(
        modifier = Modifier.fillMaxSize()
    )
}