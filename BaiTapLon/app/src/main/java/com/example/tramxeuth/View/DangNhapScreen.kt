package com.example.tramxeuth.View

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController

@Composable
fun DangNhapScreen(navController: NavController) {
    SignForm("Đăng nhập", navController)
}