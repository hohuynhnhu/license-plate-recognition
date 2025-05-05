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
    private val _isTrangthai = mutableStateOf<Boolean?>(null)
    val isTrangthai: State<Boolean?> get() = _isTrangthai

    private val _isCanhbao = mutableStateOf<Boolean?>(null)
    val isCanhbao: State<Boolean?> get() = _isCanhbao

    fun startListeningTrangthai(biensoxe: String) {
        // Lắng nghe sự thay đổi dữ liệu từ Firebase
        firebaseRepository.listenForTrangthaiChanges(biensoxe) { newIsActive ->
            // Cập nhật dữ liệu vào ViewModel
            _isTrangthai.value = newIsActive
        }
    }

    fun startListeningCanhbao(biensoxe: String) {
        // Lắng nghe sự thay đổi dữ liệu từ Firebase
        firebaseRepository.listenForCanhbaoChanges(biensoxe) { newIsActive ->
            // Cập nhật dữ liệu vào ViewModel
            _isCanhbao.value = newIsActive
        }
    }

    fun updateCarTrangthai(biensoxe: String, trangthai: Boolean){
        firebaseRepository.updateTrangThai(biensoxe, trangthai){success ->
            if (success) {
                Log.d("Firebase", "Cập nhật trạng thái thành công")
            } else {
                Log.e("Firebase", "Cập nhật thất bại")
            }
        }
    }

    fun updateCarCanhbao(biensoxe: String, canhbao: Boolean){
        firebaseRepository.updateCanhbao(biensoxe, canhbao){success ->
            if (success) {
                Log.d("Firebase", "Cập nhật trạng thái thành công")
            } else {
                Log.e("Firebase", "Cập nhật thất bại")
            }
        }
    }


}