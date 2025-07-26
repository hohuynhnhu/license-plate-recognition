package com.example.tramxeuth.View

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tramxeuth.Model.NhanVien
import com.example.tramxeuth.ViewModel.NhanVienViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import org.json.JSONObject
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import kotlin.math.log


@Composable
fun AdminHomeScreen(uid: String, navController: NavController) {
    val nhanVienViewModel: NhanVienViewModel = viewModel()
    val nhanVien = nhanVienViewModel.nhanVien
    val isPanelVisible = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        nhanVienViewModel.loadNhanVien(uid)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        nhanVien?.let { nhanVien ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFB3E5FC)),
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("THẺ NHÂN VIÊN", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Divider(color = Color.Gray)
                        Text("Họ tên: ${nhanVien.ten}", fontSize = 18.sp)
                        Text("Email: ${nhanVien.email}")
                        Text("CCCD: ${nhanVien.cccd}")
                    }
                }

                Button(
                    onClick = { isPanelVisible.value = !isPanelVisible.value },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00AEFF))
                ) {
                    Text(if (isPanelVisible.value) "Đóng ủy thác" else "Tạo ủy thác mở cổng")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { navController.navigate("lichsuyeucaubv/${nhanVien.cccd}") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00AEFF))
                ) {
                    Text("Lịch sử yêu cầu")
                    Log.d("cccd",nhanVien.cccd)
                }
            }

            AnimatedVisibility(
                visible = isPanelVisible.value,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier.align(Alignment.BottomCenter)
                    .zIndex(0f)

            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.7f)
                        .background(Color.White, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .padding(16.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    UythacPanel(nhanVien = nhanVien)
                }
            }
        } ?: CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
fun UythacPanel(nhanVien: NhanVien) {
    val timeLimit = remember { mutableStateOf(1f) }
    val qrData = remember { mutableStateOf("") }
    val now = remember { LocalDateTime.now() }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    Column {
        Text("Yêu cầu mở cổng", fontWeight = FontWeight.Bold, fontSize = 20.sp)

        Spacer(modifier = Modifier.height(8.dp))
        Text("Thời gian mở cổng: ${timeLimit.value.toInt()} giờ")

        Slider(
            value = timeLimit.value,
            onValueChange = { timeLimit.value = it },
            valueRange = 1f..24f,
            steps = 22
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val endTime = now.plusHours(timeLimit.value.toLong())
                val json = JSONObject().apply {
                    put("ten", nhanVien.ten)
                    put("cccd", nhanVien.cccd)
                    put("email", nhanVien.email)
                    put("thoigian_batdau", now.format(formatter))
                    put("thoigian_ketthuc", endTime.format(formatter))
                    put("ghichu", "Yêu cầu mở cổng được ủy thác cho nhân viên")
                }
                qrData.value = json.toString()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00AEFF))
        ) {
            Text("Tạo mã QR", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        qrData.value.takeIf { it.isNotBlank() }?.let {
            val bitmap = generateQRCodeBitmap(it)
            bitmap?.let { bmp ->
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = "QR Code",
                    modifier = Modifier
                        .size(260.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

fun generateQRCodeBitmap(data: String): Bitmap? {
    return try {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bmp.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }
        bmp
    } catch (e: Exception) {
        null
    }
}
