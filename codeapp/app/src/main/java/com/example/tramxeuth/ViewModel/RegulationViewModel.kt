package com.example.tramxeuth.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tramxeuth.Data.RegulaionRepository
import com.example.tramxeuth.Model.QuyDinh
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegulationViewModel : ViewModel() {

    private val regulationRepository = RegulaionRepository()

    // ✅ Khai báo StateFlow có thể nullable
    private val _regulation = MutableStateFlow<QuyDinh?>(null)
    val regulation: StateFlow<QuyDinh?> = _regulation

    fun loadRegulation() {
        viewModelScope.launch {
            val result = regulationRepository.getRegulation()
            _regulation.value = result
        }
    }
}
