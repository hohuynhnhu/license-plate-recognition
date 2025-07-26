package com.example.tramxeuth.Model
import com.google.firebase.Timestamp
data class LichSuBV(
    val cccd: String = "",
    val email: String = "",
    val name: String = "",
    var timeRequest:Timestamp? = null
)
