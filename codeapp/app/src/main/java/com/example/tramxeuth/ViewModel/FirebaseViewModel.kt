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

    // Auto leave states
    private val _autoLeaveEnabled = mutableStateOf(false)
    val autoLeaveEnabled: State<Boolean> get() = _autoLeaveEnabled

    private val _autoLeaveEnabledPhu = mutableStateOf(false)
    val autoLeaveEnabledPhu: State<Boolean> get() = _autoLeaveEnabledPhu

    // Functions to manage auto leave
    fun setAutoLeave(biensoxe: String, enabled: Boolean) {
        _autoLeaveEnabled.value = enabled
        firebaseRepository.updateAutoLeave(biensoxe, enabled) { success ->
            if (success) {
                Log.d("Firebase", "Cập nhật tự động rời thành công")
            } else {
                Log.e("Firebase", "Cập nhật tự động rời thất bại")
            }
        }
    }

    fun setAutoLeavePhu(biensophu: String, enabled: Boolean) {
        _autoLeaveEnabledPhu.value = enabled
        firebaseRepository.updateAutoLeave(biensophu, enabled) { success ->
            if (success) {
                Log.d("Firebase", "Cập nhật tự động rời xe phụ thành công")
            } else {
                Log.e("Firebase", "Cập nhật tự động rời xe phụ thất bại")
            }
        }
    }

    fun startListeningAutoLeave(biensoxe: String) {
        firebaseRepository.listenForAutoLeaveChanges(biensoxe) { autoLeave ->
            _autoLeaveEnabled.value = autoLeave ?: false
        }
    }

    fun startListeningAutoLeavePhu(biensophu: String) {
        firebaseRepository.listenForAutoLeaveChanges(biensophu) { autoLeave ->
            _autoLeaveEnabledPhu.value = autoLeave ?: false
        }
    }

    fun startListeningTrangthai(biensoxe: String) {
        // Lắng nghe sự thay đổi dữ liệu từ Firebase
        firebaseRepository.listenForTrangthaiChanges(biensoxe) { newIsActive ->
            // Cập nhật dữ liệu vào ViewModel
            val oldState = _isTrangthai.value
            _isTrangthai.value = newIsActive

            // Nếu xe vào bãi và AutoLeave đang bật
            if (newIsActive == true && _autoLeaveEnabled.value) {
                // Gọi cập nhật ngay lập tức
                updateCarTrangthai(biensoxe, false)
            }
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
            val oldState = _isTrangthaiPhu.value
            _isTrangthaiPhu.value = newIsActive

            if (newIsActive == true && _autoLeaveEnabledPhu.value) {
                updateCarTrangthaiPhu(biensophu, false)
            }
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