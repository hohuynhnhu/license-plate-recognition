package com.example.tramxeuth.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tramxeuth.Data.UserRepository
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

    fun loadUserData() {
        viewModelScope.launch {
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
}