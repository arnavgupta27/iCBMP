// File: app/src/main/java/com/example/icbmpfinalboss/viewmodel/FleetViewModel.kt

package com.example.icbmpfinalboss.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icbmpfinalboss.data.BmsApiRepo
import com.example.icbmpfinalboss.data.models.Prediction
import com.example.icbmpfinalboss.data.models.BmsData
import com.example.icbmpfinalboss.data.network.PredictionApiRepo
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.* // Ensure all Flow operators are imported
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

// State for Vehicle Details (Overview Screen)
sealed interface BmsUiState {
    object Loading : BmsUiState
    data class Success(val data: BmsData) : BmsUiState
    data class Error(val message: String) : BmsUiState
}

// State for Vehicle List (Fleet Screen)
sealed interface FleetListUiState {
    object Loading : FleetListUiState
    data class Success(val fleet: List<BmsData>) : FleetListUiState
    data class Error(val message: String) : FleetListUiState
}


sealed interface PredictionsUiState {
    object Loading : PredictionsUiState
    data class Success(val predictions: List<Prediction>) : PredictionsUiState
    data class Error(val message: String) : PredictionsUiState
}
class FleetViewModel : ViewModel() {

    // --- REPOSITORIES ---
    private val vehicleRepository = BmsApiRepo()
    private val predictionRepository = PredictionApiRepo()

    // --- VEHICLE STATE MANAGEMENT ---
    private val _processedFleet = MutableStateFlow<List<BmsData>>(emptyList())
    private val _selectedVehicleId = MutableStateFlow<Int?>(null)

    // THE FIX FOR FLEETLISTSTATE:
    // This now correctly maps the _processedFleet to the FleetListUiState
    // and correctly initializes to Loading.
    val fleetListState: StateFlow<FleetListUiState> = _processedFleet
        .map { fleet ->
            if (fleet.isEmpty()) {
                FleetListUiState.Loading // If processed fleet is empty, it's still loading initially
            } else {
                FleetListUiState.Success(fleet) // Otherwise, we have data.
            }
        }
        .catch { e -> emit(FleetListUiState.Error(e.message ?: "Unknown error")) } // Catch errors
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FleetListUiState.Loading // Explicit initial value
        )

    val overviewState: StateFlow<BmsUiState> =
        combine(_processedFleet, _selectedVehicleId) { fleet, selectedId ->
            if (fleet.isEmpty()) {
                return@combine BmsUiState.Loading
            }
            val targetId = selectedId ?: fleet.firstOrNull()?.vehicleId
            val vehicleData = fleet.find { it.vehicleId == targetId }

            if (vehicleData != null) {
                BmsUiState.Success(vehicleData)
            } else {
                BmsUiState.Error("Vehicle with ID $targetId not found")
            }
        }
            .catch { e -> emit(BmsUiState.Error(e.message ?: "Unknown error")) } // Catch errors
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = BmsUiState.Loading
            )

    // --- ALERT STATE MANAGEMENT ---
    private val _predictionsState = MutableStateFlow<PredictionsUiState>(PredictionsUiState.Loading)
    val predictionsState = _predictionsState.asStateFlow()


    private var pollingJob: Job? = null

    init {
        startPolling()
    }

    // --- PUBLIC FUNCTIONS FOR UI TO CALL ---

    fun selectVehicleForOverview(vehicleId: Int) {
        _selectedVehicleId.value = vehicleId
    }

    fun acknowledgePrediction(predictionId: String) {
        if (_predictionsState.value is PredictionsUiState.Success) {
            val currentPredictions = (_predictionsState.value as PredictionsUiState.Success).predictions
            _predictionsState.value = PredictionsUiState.Success(currentPredictions.filterNot { it.predictionId == predictionId })
        }
    }

    fun clearNonWarningPredictions() {
        if (_predictionsState.value is PredictionsUiState.Success) {
            val currentPredictions = (_predictionsState.value as PredictionsUiState.Success).predictions
            // We now filter by 'predictionStatus'
            _predictionsState.value = PredictionsUiState.Success(currentPredictions.filter { it.predictionStatus.equals("Warning", ignoreCase = true) })
        }
    }

    // --- DATA FETCHING ---

    private fun startPolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while(isActive) {
                // Fetch Vehicle Data
                vehicleRepository.getBmsData()
                    .onSuccess { rawDataList ->
                        if (rawDataList.isEmpty()) {
                            _processedFleet.value = emptyList() // Set to empty if no data
                            return@onSuccess
                        }
                        val groupedByVehicle = rawDataList.groupBy { it.vehicleId }
                        val processed = groupedByVehicle.map { (_, historyList) ->
                            val sortedHistory = historyList.sortedByDescending { it.createdAt }
                            val latestEntry = sortedHistory.first()
                            val socHistory = sortedHistory.map { it.stateOfCharge }.reversed()
                            val sohHistory = sortedHistory.map { it.stateOfHealth }.reversed()
                            latestEntry.socHistory = socHistory
                            latestEntry.sohHistory = sohHistory
                            latestEntry
                        }
                        _processedFleet.value = processed.sortedBy { it.vehicleId }
                    }
                    .onFailure { error ->
                        // On vehicle data fetch failure, set processed fleet to empty and handle in UI
                        _processedFleet.value = emptyList()
                        // A more robust error handling would update fleetListState to Error(message)
                    }

                // Fetch Alert Data
                predictionRepository.fetchPredictions()
                    .onSuccess { predictions ->
                        _predictionsState.value = PredictionsUiState.Success(predictions)
                    }
                    .onFailure { error ->
                        _predictionsState.value = PredictionsUiState.Error(error.message ?: "Unknown prediction error")
                    }
                delay(15000)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }
}
