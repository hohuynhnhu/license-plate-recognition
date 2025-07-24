package com.example.tramxeuth.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.tramxeuth.Data.NhanVienRepository
import com.example.tramxeuth.Model.NhanVien

class NhanVienViewModel: ViewModel()
{
    private val repository = NhanVienRepository()
    var nhanVien by mutableStateOf<NhanVien?>(null)
    private set
    fun loadNhanVien(uid: String) {
        repository.getNhanVienId(uid) {
            nhanVien = it
        }
    }
}