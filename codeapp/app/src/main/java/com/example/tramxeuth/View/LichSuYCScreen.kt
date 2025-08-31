package com.example.tramxeuth.View

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tramxeuth.ViewModel.LichSuYeuCauViewModel
import androidx.compose.material3.Button

@Composable
fun LichSuYCScreen(cccd:String,navController: NavController){
    val ViewModel: LichSuYeuCauViewModel = viewModel()
    val danhsach=ViewModel.danhSachYeuCau
    LaunchedEffect(Unit) {
        ViewModel.loadYeuCau(cccd)
    }
    Column(modifier= Modifier
        .fillMaxSize()
        .padding(16.dp)
    ){
        Button(
            onClick = { navController.popBackStack() })
        {
            Text("Quay lại")
        }
        Text("Lịch sử yêu cầu", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            if (danhsach.isEmpty()) {
                item { Text("Không có yêu cầu nào.") }
            } else {
                items(danhsach) { yeuCau ->
                    Card(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Tên: ${yeuCau.TEN}")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Email: ${yeuCau.EMAIL}")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("CCCD: ${yeuCau.CCCD}")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Thời gian bắt đầu: ${yeuCau.THOIGIAN_BATDAU}")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Thời gian kết thúc: ${yeuCau.THOIGIAN_KETTHUC}")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Duyệt: ${if (yeuCau.approve) "Đã duyệt" else "Chưa duyệt"}")
                        }
                    }
                }
            }
        }

    }}