package com.example.tramxeuth.Retrofit

import com.example.tramxeuth.ApiService.NotificationApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    private const val BASE_URL = "http://192.168.1.204:3000"

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
}
