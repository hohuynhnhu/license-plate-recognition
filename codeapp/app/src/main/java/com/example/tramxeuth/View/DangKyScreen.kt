package com.example.tramxeuth.View

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.tramxeuth.ViewModel.AuthViewModel


@Composable
fun DangKyScreen(navController: NavController, authViewModel: AuthViewModel) {
    SignForm("Đăng ký", navController, authViewModel)
}
