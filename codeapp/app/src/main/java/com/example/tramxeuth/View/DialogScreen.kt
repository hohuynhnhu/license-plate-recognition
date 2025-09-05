package com.example.tramxeuth.View

import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.tramxeuth.Model.BienSoPhu
import com.example.tramxeuth.ViewModel.FirebaseViewModel
import com.example.tramxeuth.ViewModel.NotificationViewModel
import com.example.tramxeuth.ViewModel.UserViewModel
import kotlinx.coroutines.delay

@Composable
fun CanhBaoDialog(firebaseViewModel: FirebaseViewModel, biensoxe: String) {
    val isCanhbao = firebaseViewModel.isCanhbao.value // hoặc .value nếu là State<Boolean>
    var showDialog by remember { mutableStateOf(false) }
    var thoigian by remember { mutableStateOf(10) }
    var notificationViewModel = NotificationViewModel()

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
@Composable
fun CanhBaoPhuDialog(firebaseViewModel: FirebaseViewModel, biensophu: String) {
    val isCanhbaoPhu = firebaseViewModel.isCanhbaoPhu.value
    var showDialog by remember { mutableStateOf(false) }
    var thoigian by remember { mutableStateOf(10) }

    LaunchedEffect(isCanhbaoPhu) {
        if (isCanhbaoPhu == true) {
            showDialog = true
            thoigian = 10
        }
    }

    LaunchedEffect(showDialog) {
        if (showDialog) {
            while (thoigian > 0) {
                delay(1000)
                thoigian -= 1
            }
            showDialog = false
            firebaseViewModel.updateCarCanhbaoPhu(biensophu, false)
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
                Text("Xe phụ của bạn đang chuẩn bị ra khỏi nhà xe?")
            },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    firebaseViewModel.updateCarCanhbaoPhu(biensophu, false)
                    firebaseViewModel.updateCarTrangthaiPhu(biensophu, false)
                }) {
                    Text("Vâng, đó là xe của tôi")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    firebaseViewModel.updateCarCanhbaoPhu(biensophu, false)
                }) {
                    Text("Không phải (${thoigian}s)")
                }
            }
        )
    }
}
@Composable
fun ThemXePhuDialog(
    userViewModel: UserViewModel,
    showDialog: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var bienSoPhuInput by remember { mutableStateOf("") }

    if (!showDialog) return

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Thêm xe lạ", fontSize = 22.sp, fontWeight = FontWeight.Bold) },
        text = {
            TextField(
                value = bienSoPhuInput,
                onValueChange = { bienSoPhuInput = it },
                label = { Text("Nhập biển số phụ") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = {
                if (bienSoPhuInput.isNotBlank()) {
                    userViewModel.themBienSoPhu(BienSoPhu(bienSoPhuInput.trim())) { result ->
                        when (result) {
                            BienSoPhuResult.SUCCESS -> {
                                Toast.makeText(context, "Thêm thành công", Toast.LENGTH_SHORT).show()
                                onDismiss()
                            }
                            BienSoPhuResult.DUPLICATE -> {
                                Toast.makeText(context, "Biển số đã tồn tại", Toast.LENGTH_SHORT).show()
                            }
                            BienSoPhuResult.FAIL -> {
                                Toast.makeText(context, "Thêm thất bại", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }) {
                Text("Xác nhận", fontSize = 18.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Hủy", fontSize = 18.sp)
            }
        }
    )
}
enum class BienSoPhuResult {
    SUCCESS, DUPLICATE, FAIL
}
@Composable
fun ConfirmDeleteDialog(
    biensophu: String?,
    userViewModel: UserViewModel,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Xác nhận xoá", fontWeight = FontWeight.Bold, fontSize = 22.sp)
        },
        text = {
            Text("Bạn có chắc chắn muốn xoá biển số lạ này không?", fontSize = 20.sp)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (!biensophu.isNullOrBlank()) {
                        userViewModel.xoaBienSoPhu(biensophu)
                    }
                    onDismiss()
                }
            ) {
                Text("Xoá", color = Color.Red, fontSize = 18.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Huỷ", fontSize = 18.sp)
            }
        }
    )
}
