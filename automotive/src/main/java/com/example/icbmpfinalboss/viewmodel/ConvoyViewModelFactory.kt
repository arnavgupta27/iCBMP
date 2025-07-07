package com.example.icbmpfinalboss.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.icbmpfinalboss.data.network.ConvoyRepository

class ConvoyViewModelFactory(
    private val repository: ConvoyRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConvoyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ConvoyViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
