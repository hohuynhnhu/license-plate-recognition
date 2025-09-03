package com.example.tramxeuth.Model

import com.google.gson.annotations.SerializedName

data class PaymentResponse(
    @SerializedName("paymentUrl")
    val paymentUrl: String
)