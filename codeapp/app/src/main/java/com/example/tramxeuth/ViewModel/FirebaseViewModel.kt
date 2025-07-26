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

    private val _isTrangthaiPhu = mutableStateOf<Boolean?>(null)
    val isTrangthaiPhu: State<Boolean?> get() = _isTrangthaiPhu

    private val _isCanhbaoPhu = mutableStateOf<Boolean?>(null)
    val isCanhbaoPhu: State<Boolean?> get() = _isCanhbaoPhu

    private val _timestamp = mutableStateOf<String?>(null)
    val timestamp: State<String?> get() = _timestamp

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

    fun startListeningTrangthaiPhu(biensophu: String) {
        firebaseRepository.listenForTrangthaiChanges(biensophu) { newIsActive ->
            _isTrangthaiPhu.value = newIsActive
        }
    }

    fun startListeningCanhbaoPhu(biensophu: String) {
        firebaseRepository.listenForCanhbaoChanges(biensophu) { newIsActive ->
            _isCanhbaoPhu.value = newIsActive
        }
    }

    fun updateCarTrangthaiPhu(biensophu: String, trangthai: Boolean) {
        firebaseRepository.updateTrangThai(biensophu, trangthai) { success ->
            if (success) {
                Log.d("Firebase", "Cập nhật trạng thái thành công")
            } else {
                Log.e("Firebase", "Cập nhật trạng thái thất bại")
            }
        }
    }

    fun updateCarCanhbaoPhu(biensophu: String, canhbao: Boolean) {
        firebaseRepository.updateCanhbao(biensophu, canhbao) { success ->
            if (success) {
                Log.d("Firebase", "Cập nhật cảnh báo thành công")
            } else {
                Log.e("Firebase", "Cập nhật cảnh báo thất bại")
            }
        }
    }
}