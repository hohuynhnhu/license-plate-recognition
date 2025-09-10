package com.example.tramxeuth.Retrofit

import com.example.tramxeuth.ApiService.NotificationApiService
import com.example.tramxeuth.ApiService.PaymentApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    private const val BASE_URL = "https://api-plate-vision.onrender.com"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)  // Thời gian timeout kết nối
        .writeTimeout(60, TimeUnit.SECONDS)    // Thời gian timeout ghi dữ liệu
        .readTimeout(60, TimeUnit.SECONDS)     // Thời gian timeout đọc dữ liệu
        .build()

    // Tạo instance Retrofit duy nhất
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val notificationApi: NotificationApiService by lazy { retrofit.create(NotificationApiService::class.java) }
    val paymentApi: PaymentApiService by lazy { retrofit.create(PaymentApiService::class.java) }
}
