package com.example.tramxeuth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.tramxeuth.Route.routeScreen
import com.example.tramxeuth.ViewModel.AuthViewModel
import com.example.tramxeuth.ViewModel.FirebaseViewModel
import com.example.tramxeuth.ViewModel.UserViewModel

class MainActivity : ComponentActivity() {
    private lateinit var authViewModel: AuthViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var firebaseViewModel: FirebaseViewModel

    private var routeFromNotification: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authViewModel = AuthViewModel()
        userViewModel = UserViewModel()
        firebaseViewModel = FirebaseViewModel()

        // Lấy dữ liệu route nếu được mở từ thông báo
        routeFromNotification = intent?.getStringExtra("route")
        Log.d("MainActivity", "Route from notification: $routeFromNotification")

        setContent {
            routeScreen(
                authViewModel = authViewModel,
                userViewModel = userViewModel,
                firebaseViewModel = firebaseViewModel,
            )
        }
    }
}
