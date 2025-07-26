package com.example.tramxeuth.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.tramxeuth.Data.LichSuBVRepository
import com.example.tramxeuth.Model.LichSuBV

class LichSuYeuCauViewModel:ViewModel() {
    private val repository = LichSuBVRepository()
    var danhSachYeuCau by mutableStateOf<List<LichSuBV>>(emptyList())

    fun loadYeuCau(cccd: String) {
        repository.getYeuCauByCCCD(cccd) {
            danhSachYeuCau = it
        }
    }
}