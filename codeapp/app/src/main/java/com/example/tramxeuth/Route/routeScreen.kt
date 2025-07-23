package com.example.tramxeuth.Route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tramxeuth.View.DangKyScreen
import com.example.tramxeuth.View.DangNhapScreen
import com.example.tramxeuth.View.DetailParkingScreen
import com.example.tramxeuth.View.ParkingHistoryScreen
import com.example.tramxeuth.View.ThongBaoScreen
import com.example.tramxeuth.View.homeScreen
import com.example.tramxeuth.ViewModel.AuthViewModel
import com.example.tramxeuth.ViewModel.FirebaseViewModel
import com.example.tramxeuth.ViewModel.ParkingHistoryViewModel
import com.example.tramxeuth.ViewModel.UserViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun routeScreen(authViewModel: AuthViewModel, userViewModel: UserViewModel, firebaseViewModel: FirebaseViewModel) {
    val navController = rememberNavController()
    val parkingHistoryViewModel: ParkingHistoryViewModel = viewModel()
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
        composable("parkingHistory/{biensoxe}") { backStackEntry ->
            val biensoxe = backStackEntry.arguments?.getString("biensoxe")
            if (biensoxe != null) {
                ParkingHistoryScreen(navController,parkingHistoryViewModel, biensoxe)
            }
        }
        composable("detail_parkingHistory/{date}") { backStackEntry ->
            val date = backStackEntry.arguments?.getString("date")
            if (date != null) {
                DetailParkingScreen(parkingHistoryViewModel, date)
            }
        }
    }
}