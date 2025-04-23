package com.example.tramxeuth.ViewModel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.tramxeuth.Data.CarRepository

// ViewModel
class FirebaseViewModel() : ViewModel() {
    private val firebaseRepository = CarRepository()
    // MutableState để lưu giá trị 'isActive'
    private val _isActive = mutableStateOf<Boolean?>(null)
    val isActive: State<Boolean?> get() = _isActive

    fun startListening(biensoxe: String) {
        // Lắng nghe sự thay đổi dữ liệu từ Firebase
        firebaseRepository.listenForIsActiveChanges(biensoxe) { newIsActive ->
            // Cập nhật dữ liệu vào ViewModel
            _isActive.value = newIsActive
        }
    }

    fun updateCar(biensoxe: String, trangthai: Boolean){
        firebaseRepository.updateTrangThai(biensoxe, trangthai){success ->
            if (success) {
                Log.d("Firebase", "Cập nhật trạng thái thành công")
            } else {
                Log.e("Firebase", "Cập nhật thất bại")
            }
        }
    }
}