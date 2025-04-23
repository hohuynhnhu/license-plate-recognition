    package com.example.tramxeuth

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import com.example.tramxeuth.Route.routeScreen
import com.example.tramxeuth.ViewModel.AuthViewModel
import com.example.tramxeuth.ViewModel.FirebaseViewModel
import com.example.tramxeuth.ViewModel.UserViewModel

    class MainActivity : ComponentActivity() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onCreate(savedInstanceState: Bundle?) {
            var authViewModel = AuthViewModel()
            var userViewModel = UserViewModel()
            val firebaseViewModel = FirebaseViewModel()

            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            setContent {
                routeScreen(authViewModel, userViewModel, firebaseViewModel)
//                MainScreen()
            }
        }
    }
