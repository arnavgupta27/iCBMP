// File: app/src/main/java/com/example/icbmpfinalboss/viewmodel/FleetViewModel.kt

package com.example.icbmpfinalboss.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icbmpfinalboss.data.BmsApiRepo
import com.example.icbmpfinalboss.data.models.BmsData
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

// State for the Overview screen (a single item)
sealed interface BmsUiState {
    object Loading : BmsUiState
    data class Success(val data: BmsData) : BmsUiState
    data class Error(val message: String) : BmsUiState
}

// State for the Fleet screen (a list of items)
sealed interface FleetListUiState {
    object Loading : FleetListUiState
    data class Success(val fleet: List<BmsData>) : FleetListUiState
    data class Error(val message: String) : FleetListUiState
}

class FleetViewModel : ViewModel() {

    private val repository = BmsApiRepo()

    // StateFlow for the Overview screen's data
    private val _overviewState = MutableStateFlow<BmsUiState>(BmsUiState.Loading)
    val overviewState = _overviewState.asStateFlow()

    // StateFlow for the Fleet screen's data
    private val _fleetListState = MutableStateFlow<FleetListUiState>(FleetListUiState.Loading)
    val fleetListState = _fleetListState.asStateFlow()

    private var pollingJob: Job? = null

    init {
        startPolling()
    }

    private fun startPolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while(isActive) {
                repository.getBmsData()
                    .onSuccess { dataList ->
                        if (dataList.isNotEmpty()) {
                            // First item goes to the Overview screen
                            _overviewState.value = BmsUiState.Success(dataList.first())

                            // The rest of the list goes to the Fleet screen
                            _fleetListState.value = FleetListUiState.Success(dataList.drop(1))
                        } else {
                            // Handle case where API returns an empty list
                            _overviewState.value = BmsUiState.Error("No data available.")
                            _fleetListState.value = FleetListUiState.Error("No fleet vehicles found.")
                        }
                    }
                    .onFailure { error ->
                        // If the call fails, put both screens in an error state
                        val errorMessage = error.message ?: "Unknown network error"
                        _overviewState.value = BmsUiState.Error(errorMessage)
                        _fleetListState.value = FleetListUiState.Error(errorMessage)
                    }
                delay(5000) // Poll every 5 seconds
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }
}
