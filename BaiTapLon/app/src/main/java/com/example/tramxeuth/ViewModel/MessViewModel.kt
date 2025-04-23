package com.example.tramxeuth.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MessViewModel : ViewModel() {

    private val _fcmToken = MutableStateFlow("")
    val fcmToken = _fcmToken.asStateFlow()

    init {
        getFirebaseToken()
    }

    private fun getFirebaseToken() {
        viewModelScope.launch {
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val token = task.result
                        Log.d("FCM", "Token: $token")
                        _fcmToken.value = token
                    } else {
                        Log.e("FCM", "Failed to get FCM token", task.exception)
                    }
                }
        }
    }
}