package com.example.tramxeuth.Route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tramxeuth.View.DangKyScreen
import com.example.tramxeuth.View.DangNhapScreen
import com.example.tramxeuth.View.ThongBaoScreen
import com.example.tramxeuth.View.homeScreen
import com.example.tramxeuth.ViewModel.AuthViewModel
import com.example.tramxeuth.ViewModel.FirebaseViewModel
import com.example.tramxeuth.ViewModel.UserViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun routeScreen(authViewModel: AuthViewModel, userViewModel: UserViewModel, firebaseViewModel: FirebaseViewModel) {
    val navController = rememberNavController()
    LaunchedEffect(Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            navController.navigate("home")
        } else {
            navController.navigate("login")
        }
    }
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { DangNhapScreen(navController, authViewModel) }
        composable("logup") { DangKyScreen(navController, authViewModel)}
        composable("home") { homeScreen(navController, authViewModel, userViewModel, firebaseViewModel) }
        composable("noti") { ThongBaoScreen(navController) }
    }
}