package com.example.tramxeuth.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tramxeuth.Model.Request.NotificationRequest
import com.example.tramxeuth.Retrofit.RetrofitInstance
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class NotificationViewModel : ViewModel() {
    private val _status = MutableStateFlow<String?>(null)
    val status: StateFlow<String?> = _status

    private val auth = FirebaseAuth.getInstance()
    private val uid = auth.currentUser?.uid

    fun sendNotification(body: String) {
        viewModelScope.launch {
            try {
                val userDoc = uid?.let {
                    FirebaseFirestore.getInstance()
                        .collection("thongtindangky")
                        .document(it)
                        .get()
                        .await()
                }

                val deviceIds = userDoc?.get("fcmTokens") as? List<String> ?: emptyList()

                if (deviceIds.isEmpty()) {
                    _status.value = "Lỗi chưa có FCM token"
                    return@launch
                }

                val request = NotificationRequest(
                    title = "Thông báo!",
                    body = body,
                    deviceId = deviceIds
                )

                val response = RetrofitInstance.notificationApi.sendNotification(request)
                _status.value = if (response.isSuccessful) "Đã gửi thông báo" else "Lỗi: ${response.code()}"
            } catch (e: Exception) {
                _status.value = "Exception: ${e.message}"
            }
        }
    }
}
