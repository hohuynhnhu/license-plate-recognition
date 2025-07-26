package com.example.tramxeuth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.tramxeuth.Route.routeScreen
import com.example.tramxeuth.ViewModel.*
import com.google.firebase.auth.FirebaseAuth
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.tramxeuth.View.ParkingHistoryScreen
import com.example.tramxeuth.ViewModel.AuthViewModel
import com.example.tramxeuth.ViewModel.FirebaseViewModel
import com.example.tramxeuth.ViewModel.ParkingHistoryViewModel
import com.example.tramxeuth.ViewModel.UserViewModel

class MainActivity : ComponentActivity() {
    private lateinit var authViewModel: AuthViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var firebaseViewModel: FirebaseViewModel
    private lateinit var regulationViewModel: RegulationViewModel
    private lateinit var parkingHistoryViewModel: ParkingHistoryViewModel

    private var currentRoute: MutableState<String?> = mutableStateOf(null)
    private var routeFromNotification: String? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createDefaultNotificationChannel(this)

        authViewModel = AuthViewModel()
        userViewModel = UserViewModel()
        firebaseViewModel = FirebaseViewModel()
        regulationViewModel = RegulationViewModel()
        parkingHistoryViewModel = ParkingHistoryViewModel()

        Log.d("MainActivity", "Received intent route: ${intent.getStringExtra("route")}")

        handleIntentRoute(intent)

        setContent {
            currentRoute.value?.let { route ->
                routeScreen(
                    startDestination = route,
                    authViewModel = authViewModel,
                    userViewModel = userViewModel,
                    firebaseViewModel = firebaseViewModel,
                    regulationViewModel = regulationViewModel,
                    parkingHistoryViewModel = parkingHistoryViewModel,
                )
            }
        }
    }

    fun createDefaultNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "default"
            val channelName = "Thông báo chung"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Kênh thông báo mặc định cho ứng dụng"
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }


//    // 🔁 Nhận intent mới từ thông báo khi activity đã mở
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntentRoute(intent)
    }

    private fun handleIntentRoute(intent: Intent?) {
        val route = intent?.getStringExtra("route")
        Log.d("MainActivity", "Received intent route: ${route}")
        if (route != null) {
            currentRoute.value = route
        } else {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                authViewModel.getUserRole { role ->
                    currentRoute.value = when (role) {
                        "user" -> "home"
                        "admin" -> "admin"
                        else -> "login"
                    }
                }
            } else {
                currentRoute.value = "login"
            }
        }
    }
}
