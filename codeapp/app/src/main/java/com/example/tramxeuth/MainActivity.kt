package com.example.tramxeuth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tramxeuth.Route.routeScreen
import com.example.tramxeuth.View.ParkingHistoryScreen
import com.example.tramxeuth.ViewModel.AuthViewModel
import com.example.tramxeuth.ViewModel.FirebaseViewModel
import com.example.tramxeuth.ViewModel.ParkingHistoryViewModel
import com.example.tramxeuth.ViewModel.UserViewModel
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    private lateinit var authViewModel: AuthViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var firebaseViewModel: FirebaseViewModel
    private lateinit var parkingHistoryViewModel: ParkingHistoryViewModel

    private var routeFromNotification: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authViewModel = AuthViewModel()
        userViewModel = UserViewModel()
        firebaseViewModel = FirebaseViewModel()
        parkingHistoryViewModel = ParkingHistoryViewModel()

        val user = FirebaseAuth.getInstance().currentUser

        var startDestination = "login"
        if (user != null) {
            authViewModel.getUserRole{
                    role->
                if(role == "user"){
                    startDestination = "home"
                }else{
                    startDestination = "admin"
                }}
        } else {
            startDestination = "login"
        }

        // Lấy dữ liệu route nếu được mở từ thông báo
        routeFromNotification = intent?.getStringExtra("route")
        Log.d("MainActivity", "Route from notification: $routeFromNotification")

        setContent {
            routeScreen(
                startDestination = startDestination,
                authViewModel = authViewModel,
                userViewModel = userViewModel,
                firebaseViewModel = firebaseViewModel,
                parkingHistoryViewModel = parkingHistoryViewModel,
            )
//                MainScreen()
//                ParkingHistoryScreen("123123")
        }
    }
}
