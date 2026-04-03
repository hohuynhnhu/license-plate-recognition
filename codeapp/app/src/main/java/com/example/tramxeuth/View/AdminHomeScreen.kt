package com.example.tramxeuth.View

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tramxeuth.Model.NhanVien
import com.example.tramxeuth.ViewModel.AuthViewModel
import com.example.tramxeuth.ViewModel.NhanVienViewModel
import com.example.tramxeuth.ViewModel.UserViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import org.json.JSONObject
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

// ===== QR BITMAP =====
fun generateQRCodeBitmap(data: String): Bitmap? {
    return try {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bmp = Bitmap.createBitmap(
            IntArray(width * height) { index ->
                if (bitMatrix[index % width, index / width]) android.graphics.Color.BLACK
                else android.graphics.Color.WHITE
            },
            width, height, Bitmap.Config.RGB_565
        )
        bmp
    } catch (e: Exception) {
        null
    }
}

// ===== ADMIN HOME =====
@Composable
fun AdminHomeScreen(
    uid: String,
    navController: NavController,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel
) {
    val nhanVienViewModel: NhanVienViewModel = viewModel()
    val nhanVien = nhanVienViewModel.nhanVien
    val isPanelVisible = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { nhanVienViewModel.loadNhanVien(uid) }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF0F4F8))) {
        nhanVien?.let { nv ->
            Column(modifier = Modifier.fillMaxSize()) {

                // ===== TOP SECTION (Header + Info Card overlap) =====
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 1. Background Header Gradient
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp) // Cố định chiều cao an toàn
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF0077CC), Color(0xFF00AEFF))
                                )
                            )
                            .padding(top = 24.dp, start = 20.dp, end = 20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                                Text(
                                    "QUẢN TRỊ VIÊN",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f),
                                    letterSpacing = 1.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    nv.ten,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    nv.email,
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.9f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            TextButton(
                                onClick = {
                                    authViewModel.logout {
                                        userViewModel.clearUserData()
                                        navController.navigate("login")
                                    }
                                },
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                                    .border(0.5.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                            ) {
                                Text("Đăng xuất", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    // 2. Info Chips Card (Đè lên biên dưới của Header dùng padding thay vì offset)
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 140.dp, start = 16.dp, end = 16.dp) // Đẩy top xuống để đè nhẹ lên nền
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            InfoChip(label = "CCCD", value = nv.cccd, modifier = Modifier.weight(1f))
                            InfoChip(label = "Chức vụ", value = "Nhân viên", modifier = Modifier.weight(1f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ===== MENU GRID =====
                Text(
                    "CHỨC NĂNG",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f) // Giúp Grid tự giãn ra hết phần còn lại, không đè vỡ layout
                ) {
                    item {
                        MenuCard(
                            title = "Tạo ủy thác mở cổng",
                            iconBg = Color(0xFFE6F1FB),
                            icon = Icons.Default.QrCode,
                            iconTint = Color(0xFF185FA5),
                            onClick = { isPanelVisible.value = true }
                        )
                    }
                    item {
                        MenuCard(
                            title = "Lịch sử yêu cầu",
                            iconBg = Color(0xFFE1F5EE),
                            icon = Icons.Default.History,
                            iconTint = Color(0xFF0F6E56),
                            onClick = { navController.navigate("lichsuyeucaubv/${nv.cccd}") }
                        )
                    }
                    item {
                        MenuCard(
                            title = "Lịch sử hoạt động",
                            iconBg = Color(0xFFFAEEDA),
                            icon = Icons.Default.DateRange,
                            iconTint = Color(0xFF854F0B),
                            onClick = { navController.navigate("lichsuhoatdong") }
                        )
                    }
                    item {
                        MenuCard(
                            title = "Quản lý xe trong bãi",
                            iconBg = Color(0xFFEEEDFE),
                            icon = Icons.Default.DirectionsCar,
                            iconTint = Color(0xFF534AB7),
                            onClick = { navController.navigate("parking") }
                        )
                    }
                }
            }

            // ===== SCRIM (Nền mờ đen phía sau Bottom Sheet) =====
            AnimatedVisibility(
                visible = isPanelVisible.value,
                enter = fadeIn(tween(300)),
                exit = fadeOut(tween(300))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null // Tắt hiệu ứng ripple khi nhấn vào nền mờ
                        ) { isPanelVisible.value = false }
                )
            }

            // ===== BOTTOM SHEET ỦY THÁC =====
            AnimatedVisibility(
                visible = isPanelVisible.value,
                enter = slideInVertically(initialOffsetY = { fullHeight -> fullHeight }, animationSpec = tween(300)),
                exit = slideOutVertically(targetOffsetY = { fullHeight -> fullHeight }, animationSpec = tween(300)),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.7f) // Giới hạn chiều cao an toàn
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .background(Color.White)
                ) {
                    UythacPanel(nhanVien = nv, onClosePanel = { isPanelVisible.value = false })
                }
            }

        } ?: CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color(0xFF0077CC))

        // Cần đảm bảo component này không tự sinh ra khoảng trắng lỗi (tuỳ vào code bên trong)
        // BaoRungDialog()
    }
}

// ===== UYTHAC PANEL =====
@Composable
fun UythacPanel(
    nhanVien: NhanVien,
    onClosePanel: () -> Unit = {}
) {
    val timeLimit = remember { mutableStateOf(1f) }
    val qrData = remember { mutableStateOf("") }
    val now = remember { LocalDateTime.now() }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    // Thêm verticalScroll để khi tạo QR Code hoặc ở màn nhỏ, nội dung không bị cắt mất
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(scrollState)
    ) {
        // Thanh gạt (Drag Handle)
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(4.dp)
                .background(Color.LightGray, RoundedCornerShape(2.dp))
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Ủy thác mở cổng", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF1A1A1A))
            TextButton(
                onClick = { onClosePanel() },
                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF185FA5)),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Text("Đóng", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Thời gian hiệu lực: ${timeLimit.value.toInt()} giờ",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color.DarkGray
        )
        Spacer(modifier = Modifier.height(8.dp))

        Slider(
            value = timeLimit.value,
            onValueChange = { newValue: Float -> timeLimit.value = newValue },
            valueRange = 1f..24f,
            steps = 22,
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF00AEFF),
                activeTrackColor = Color(0xFF00AEFF),
                inactiveTrackColor = Color(0xFF00AEFF).copy(alpha = 0.2f)
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val endTime = now.plusHours(timeLimit.value.toLong())
                val json = JSONObject().apply {
                    put("ten", nhanVien.ten)
                    put("cccd", nhanVien.cccd)
                    put("email", nhanVien.email)
                    put("thoigian_batdau", now.format(formatter))
                    put("thoigian_ketthuc", endTime.format(formatter))
                }
                qrData.value = json.toString()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00AEFF))
        ) {
            Text("Tạo mã QR", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Vùng hiển thị QR Code
        qrData.value.takeIf { it.isNotBlank() }?.let { data: String ->
            val bitmap = generateQRCodeBitmap(data)
            bitmap?.let { bmp: Bitmap ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFAFAFA), RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(16.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = "QR Code",
                        modifier = Modifier.size(220.dp)
                    )
                }
            }
        }
    }
}

// ===== COMPONENTS =====
@Composable
fun InfoChip(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color(0xFFF0F7FF), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Text(label, fontSize = 11.sp, color = Color(0xFF185FA5), letterSpacing = 0.8.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF042C53),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun MenuCard(
    title: String,
    iconBg: Color,
    icon: ImageVector,
    iconTint: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp), // Tăng bóng nhẹ cho đẹp
        border = BorderStroke(0.5.dp, Color(0xFFEAEAEA)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(iconBg, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A),
                lineHeight = 20.sp
            )
        }
    }
}