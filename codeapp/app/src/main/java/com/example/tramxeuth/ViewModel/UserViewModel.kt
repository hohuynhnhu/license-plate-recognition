package com.example.tramxeuth.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tramxeuth.Data.GiaHanResult
import com.example.tramxeuth.Data.UserRepository
import com.example.tramxeuth.Model.BienSoPhu
import com.example.tramxeuth.Model.biensotrongbai
import com.example.tramxeuth.Model.thongtindangky
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val userRepository = UserRepository()
    var currentUser by mutableStateOf<thongtindangky?>(null)
        private set

    var bienSoPhuOperationResult by mutableStateOf<Boolean?>(null) //biến hiển thị thông báo thành công/thất bại trên màn hình.
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

    fun themBienSoPhu(bienSo: BienSoPhu) {
        viewModelScope.launch {
            val success = userRepository.themBienSoPhu(bienSo)
            bienSoPhuOperationResult = success
            if (success) {
                loadUserData() // reload lại dữ liệu sau khi thêm
            }
        }
    }
    fun xoaBienSoPhu(bienSo: String) {
        viewModelScope.launch {
            bienSoPhuOperationResult = null
            val success = userRepository.xoaBienSoPhu(bienSo)
            bienSoPhuOperationResult = success
            if (success) {
                loadUserData() // reload lại dữ liệu sau khi xóa
            }
        }
    }
    var giaHanMessage by mutableStateOf<String?>(null)
        private set

    fun giaHanBienSoPhu(bienSo: String) {
        viewModelScope.launch {
            when (val result = userRepository.giaHanBienSoPhu(bienSo)) {
                GiaHanResult.SUCCESS -> {
                    giaHanMessage = "Gia hạn thành công"
                    loadUserData()
                }
                GiaHanResult.ALREADY_EXTENDED -> {
                    giaHanMessage = "Bạn đã gia hạn rồi"
                }
                GiaHanResult.ERROR -> {
                    giaHanMessage = "Gia hạn thất bại"
                }
            }
        }
    }
    fun clearGiaHanMessage() {
        giaHanMessage = null
    }

}