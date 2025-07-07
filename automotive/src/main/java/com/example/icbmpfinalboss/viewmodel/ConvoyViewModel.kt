package com.example.icbmpfinalboss.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icbmpfinalboss.data.models.ConvoyRecommendation
import com.example.icbmpfinalboss.data.models.PredictionNew
import com.example.icbmpfinalboss.data.network.ConvoyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ConvoyViewModel(
    private val convoyRepository: ConvoyRepository
) : ViewModel() {

    private val _convoyRecommendations = MutableStateFlow<List<ConvoyRecommendation>>(emptyList())
    val convoyRecommendations: StateFlow<List<ConvoyRecommendation>> = _convoyRecommendations

    private val _convoyPredictions = MutableStateFlow<List<PredictionNew>>(emptyList())
    val convoyPredictions: StateFlow<List<PredictionNew>> = _convoyPredictions

    fun fetchConvoyData() {
        viewModelScope.launch {
            try {
                val response = convoyRepository.fetchConvoyDataWithRetry()
                _convoyRecommendations.value = response.data.convoy_recommendations
                _convoyPredictions.value = response.data.predictions
            } catch (e: Exception) {
                // Optionally handle error
            }
        }
    }

    fun acknowledgeConvoyRecommendation(vehicleId: Int) {
        _convoyRecommendations.value = _convoyRecommendations.value.filterNot { it.vehicle_id == vehicleId }
    }

    fun acknowledgeConvoyPrediction(vehicleId: Int) {
        _convoyPredictions.value = _convoyPredictions.value.filterNot { it.vehicle_id == vehicleId }
    }
}
