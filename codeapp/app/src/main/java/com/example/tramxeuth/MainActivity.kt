package com.example.tramxeuth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tramxeuth.Route.routeScreen
import com.example.tramxeuth.View.ParkingHistoryScreen
//import com.example.tramxeuth.View.ZXING_REQUEST_CODE
import com.example.tramxeuth.ViewModel.AuthViewModel
import com.example.tramxeuth.ViewModel.FirebaseViewModel
import com.example.tramxeuth.ViewModel.ParkingHistoryViewModel
import com.example.tramxeuth.ViewModel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
//import com.google.zxing.integration.android.IntentIntegrator

class MainActivity : ComponentActivity() {
    private lateinit var authViewModel: AuthViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var firebaseViewModel: FirebaseViewModel
    private lateinit var parkingHistoryViewModel: ParkingHistoryViewModel

    private var routeFromNotification: String? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

//        if (requestCode == ZXING_REQUEST_CODE) {
//            val result = IntentIntegrator.parseActivityResult(resultCode, data)
//            if (result != null) {
//                if (result.contents != null) {
//                    // 👉 Đây là mã QR quét được
//                    Log.d("QR", "QR Code: ${result.contents}")
//                    // Gửi về Compose nếu cần
//                } else {
//                    Toast.makeText(this, "Không quét được mã.", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
    }
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
