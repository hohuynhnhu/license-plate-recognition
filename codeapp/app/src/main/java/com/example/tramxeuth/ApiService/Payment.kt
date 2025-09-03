package com.example.tramxeuth.ApiService

import com.example.tramxeuth.Model.PaymentResponse
import com.example.tramxeuth.Model.Request.PaymentRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PaymentApiService {
    @POST("payment/create")
    suspend fun createPaymentUrl(
        @Body request: PaymentRequest
    ): Response<PaymentResponse>
}