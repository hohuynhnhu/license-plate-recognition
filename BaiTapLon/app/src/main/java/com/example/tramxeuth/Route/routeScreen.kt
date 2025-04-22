package com.example.tramxeuth.Route

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tramxeuth.View.DangKyScreen
import com.example.tramxeuth.View.DangNhapScreen
import com.example.tramxeuth.View.ThongBaoScreen
import com.example.tramxeuth.View.homeScreen

@Composable
fun routeScreen() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") { DangNhapScreen(navController) }
        composable("logup") { DangKyScreen(navController)}
        composable("home") { homeScreen(navController) }
        composable("noti") { ThongBaoScreen(navController) }
    }
}