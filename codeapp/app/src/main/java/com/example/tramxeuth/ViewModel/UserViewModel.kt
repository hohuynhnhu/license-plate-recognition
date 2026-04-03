package com.example.tramxeuth.ViewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tramxeuth.Data.UserRepository
import com.example.tramxeuth.Model.BienSoPhu
import com.example.tramxeuth.Model.thongtindangky
import com.example.tramxeuth.View.BienSoPhuResult
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel: ViewModel() {
    private val userRepository = UserRepository()
    var currentUser by mutableStateOf<thongtindangky?>(null)
        private set

    var bienSoPhuOperationResult by mutableStateOf<Boolean?>(null) //biến hiển thị thông báo thành công/thất bại trên màn hình.
        private set

    var thongBaoXoaPhu by mutableStateOf<String?>(null)
        private set

    fun loadUserData() {
        viewModelScope.launch {
            userRepository.removeExpiredBienSoPhu()
            currentUser = userRepository.getCurrentUser()
            currentUser?.biensoxe?.let { biensoxe ->
                val topic = biensoxe
                FirebaseMessaging.getInstance().subscribeToTopic(topic)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("FCM", "Đăng ký topic '$topic' thành công")
                        } else {
                            Log.e("FCM", "Lỗi khi đăng ký topic", task.exception)
                        }
                    }
            }
        }
    }
    fun clearUserData() {
        currentUser = null
    }

    fun themBienSoPhu(
        bienSo: BienSoPhu,
        callback: (BienSoPhuResult) -> Unit
    ) {
        viewModelScope.launch {
            val isDuplicate = userRepository.isBienSoTrung(bienSo.bienSo)
            if (isDuplicate) {
                callback(BienSoPhuResult.DUPLICATE)
                return@launch
            }

            val success = userRepository.themBienSoPhu(bienSo)
            if (success) {
                loadUserData()
                callback(BienSoPhuResult.SUCCESS)
            } else {
                callback(BienSoPhuResult.FAIL)
            }
        }
    }

    fun xoaBienSoPhu(bienSo: String) {
        viewModelScope.launch {
            try {
                val success = userRepository.xoaBienSoPhu(bienSo)
                thongBaoXoaPhu = if (success) {
                    loadUserData()
                    "Xóa biển số lạ thành công"
                } else {
                    "Không thể xóa vì xe chưa rời khỏi bãi"
                }
            } catch (e: Exception) {
                thongBaoXoaPhu = "Đã xảy ra lỗi: ${e.message}"
            }
        }
    }

    var giaHanMessage by mutableStateOf<String?>(null)
        private set

    fun giaHanBienSoPhu(bienSo: String) {
        viewModelScope.launch {
            val result = userRepository.giaHanBienSoPhu(bienSo)
            if (result) {
                giaHanMessage = "Gia hạn thành công"
                loadUserData()
            } else {
                giaHanMessage = "Gia hạn thất bại"
            }
        }
    }

    fun clearGiaHanMessage() {
        giaHanMessage = null
    }
    fun clearThongBaoXoaPhu() {
        thongBaoXoaPhu = null
    }

}