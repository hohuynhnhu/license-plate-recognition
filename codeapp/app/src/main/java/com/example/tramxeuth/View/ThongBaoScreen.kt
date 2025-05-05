package com.example.tramxeuth.View

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ThongBaoScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp, start = 20.dp, end = 20.dp)
    ) {
        topBar(navController)
        Spacer(modifier = Modifier.height(25.dp))
        thongbaoForm("Đã gửi xe", "21/4/2025", "30 phút")
        thongbaoForm("Chuẩn bị ra", "20/4/2025", "30 phút")
        thongbaoForm("Đã rời nhà xe", "19/4/2025", "30 phút")
    }
}

@Composable
fun topBar(navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = {navController.popBackStack()}
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = ""
            )
        }
        Text(
            text = "Thông báo",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )
        Text("       ")
    }
}

@Composable
fun thongbaoForm(noidung: String, ngay: String, thoigian: String) {
    Card(
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xCCADD8E6)
        )
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Text(
                text = noidung,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
                )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(ngay)
                Text("${thoigian} trước")
            }
        }
    }
}