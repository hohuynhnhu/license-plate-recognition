package com.example.tramxeuth.Model.Request

data class PaymentRequest(
    val amount: Int, // Số tiền cần thanh toán
    val soluot: Int,
    val userId: String,
)
