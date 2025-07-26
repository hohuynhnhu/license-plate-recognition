package com.example.tramxeuth.Model.Request

data class NotificationRequest(
    val title: String,
    val body: String,
    val deviceId: List<String>
)