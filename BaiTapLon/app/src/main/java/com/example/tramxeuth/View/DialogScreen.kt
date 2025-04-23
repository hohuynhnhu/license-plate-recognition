package com.example.tramxeuth.View

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.tramxeuth.ViewModel.FirebaseViewModel
import kotlinx.coroutines.delay

@Composable
fun CanhBaoDialog(firebaseViewModel: FirebaseViewModel, biensoxe: String) {
    val isCanhbao = firebaseViewModel.isCanhbao.value // hoặc .value nếu là State<Boolean>
    var showDialog by remember { mutableStateOf(false) }
    var thoigian by remember { mutableStateOf(10) }

    LaunchedEffect(isCanhbao) {
        if (isCanhbao == true) {
            showDialog = true
            thoigian = 10 // Reset thời gian mỗi lần hiển thị
        }
    }

// Đếm ngược khi showDialog = true
    LaunchedEffect(showDialog) {
        if (showDialog) {
            while (thoigian > 0) {
                delay(1000)
                thoigian -= 1
            }
            // Khi đếm về 0 thì tự động đóng dialog và thực hiện dismiss
            showDialog = false
            firebaseViewModel.updateCarCanhbao(biensoxe, false)
            // Tùy chọn: có thể gửi thêm thông báo hoặc xử lý logic ở đây
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
            },
            title = {
                Text(text = "Cảnh báo")
            },
            text = {
                Text("Bạn đang chuẩn bị ra khỏi nhà xe đúng không?")
            },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    firebaseViewModel.updateCarCanhbao(biensoxe, false)
                    firebaseViewModel.updateCarTrangthai(biensoxe, false)
                }) {
                    Text("Vâng, đó là tôi")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    firebaseViewModel.updateCarCanhbao(biensoxe, false)
                }) {
                    Text("Không phải tôi (${thoigian}s)")
                }
            }
        )
    }
}