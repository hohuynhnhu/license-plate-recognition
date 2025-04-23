package com.example.tramxeuth.View

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.tramxeuth.ViewModel.AuthViewModel

@Composable
fun DangNhapScreen(navController: NavController, authViewModel: AuthViewModel) {
    SignForm("Đăng nhập", navController, authViewModel)
}