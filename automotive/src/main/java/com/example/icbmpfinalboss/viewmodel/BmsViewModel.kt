package com.example.icbmpfinalboss.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icbmpfinalboss.data.BmsData
import com.example.icbmpfinalboss.data.BmsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class BmsViewModel(private val repository: BmsRepository = BmsRepository()) : ViewModel() {

    private val _bmsState = MutableStateFlow(BmsData())
    val bmsState: StateFlow<BmsData> = _bmsState.asStateFlow()

    // Control states
    private val _isChargingEnabled = MutableStateFlow(false)
    val isChargingEnabled: StateFlow<Boolean> = _isChargingEnabled.asStateFlow()

    private val _isBalancingForced = MutableStateFlow(false)
    val isBalancingForced: StateFlow<Boolean> = _isBalancingForced.asStateFlow()

    init {
        // Start collecting the data stream as soon as the ViewModel is created
        viewModelScope.launch {
            repository.getBmsDataStream().collect { newData ->
                _bmsState.value = newData
            }
        }
    }

    // Functions to be called from the UI to change control states
    fun setCharging(enabled: Boolean) {
        _isChargingEnabled.value = enabled
        // Here you would normally send a command to the hardware
    }

    fun setBalancing(enabled: Boolean) {
        _isBalancingForced.value = enabled
    }
}
