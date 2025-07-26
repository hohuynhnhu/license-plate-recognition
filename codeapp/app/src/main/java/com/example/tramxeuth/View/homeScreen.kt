package com.example.tramxeuth.View

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TagFaces
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tramxeuth.R
import com.example.tramxeuth.ViewModel.AuthViewModel
import com.example.tramxeuth.ViewModel.FirebaseViewModel
import com.example.tramxeuth.ViewModel.NotificationViewModel
import com.example.tramxeuth.ViewModel.UserViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun homeScreen(navController: NavController, authViewModel: AuthViewModel, userViewModel: UserViewModel, firebaseViewModel: FirebaseViewModel) {
    val user = userViewModel.currentUser
    val isTrangthai = firebaseViewModel.isTrangthai.value
    val isTrangthaiPhu = firebaseViewModel.isTrangthaiPhu.value

    LaunchedEffect(user) {
        if (user == null)
            userViewModel.loadUserData()
        else{
            firebaseViewModel.startListeningTrangthai(user.biensoxe)
            firebaseViewModel.startListeningCanhbao(user.biensoxe)

            user.biensophu?.bienSo?.let { bienSoPhu ->
                firebaseViewModel.startListeningTrangthaiPhu(bienSoPhu)
                firebaseViewModel.startListeningCanhbaoPhu(bienSoPhu)
            }
        }

    }
    Box(
        modifier = Modifier.fillMaxSize()
            .padding(bottom = 30.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_uth),
            contentDescription = "background UTH",
            contentScale = ContentScale.FillBounds, // hoặc .Fit tùy nhu cầu
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .offset(x = 0.dp, y = -40.dp),

        )
        Card(
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xCC003153)
            ),
            shape = RoundedCornerShape(0.dp)
        ) { 
            topLayout(navController, user?.ten)
        }
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 150.dp),
            shape = RoundedCornerShape(topEnd = 30.dp, topStart = 30.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    thongtinsinhvien(user?.email, user?.ten, user?.cccd)
                }
                item {
                    user?.biensoxe?.let { CanhBaoDialog(firebaseViewModel, it) }
                }
                item {
                    user?.biensophu?.bienSo?.let { CanhBaoPhuDialog(firebaseViewModel, it) }
                }
                item {
                    thongtinxe(user?.biensoxe, isTrangthai, firebaseViewModel)
                }
                item {
                    user?.biensophu?.let { xePhu ->
                        thongtinxePhu(
                            biensophu = xePhu.bienSo,
                            trangthaiPhu = isTrangthaiPhu,
                            ngayHetHan = xePhu.ngayHetHan?.toDateString(),
                            firebaseViewModel = firebaseViewModel,
                            userViewModel = userViewModel
                        )
                    }
                }
            }
        }
        // Nút Thêm xe lạ
        if (user?.biensophu == null) {
            ThemXePhuButton(userViewModel = userViewModel)
        }
        Button(
            onClick = {
                if (user != null) {
                    navController.navigate("parkingHistory/${user.biensoxe}")
                    Log.d("biensoxe", user.biensoxe)
                }
            },
            modifier = Modifier.align(Alignment.BottomStart)
        ) {
            Text("Lịch sử")
        }
        Button(
            onClick = { authViewModel.logout({
                    userViewModel.clearUserData()
                    navController.navigate("login")
                }) },
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            Text("Đăng xuất")
        }
    }
}

@Composable
fun topLayout(navController: NavController, ten: String?) {
    var notificationViewModel = NotificationViewModel()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 50.dp, horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xB3FFFFFF)
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "",
                    modifier = Modifier.size(50.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Xin chào!",
                        fontSize = 17.sp,
                    )
                    ten?.let {
                        Text(
                            text = it,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF003153)
                        )
                    }
                }
            }

        }
        IconButton(onClick = { notificationViewModel.sendNotification("Đăng xuất") }) {
            Icon(
                imageVector = Icons.Default.TagFaces,
                contentDescription = "",
                modifier = Modifier.size(50.dp),
                tint = Color(0xFFFFC107)
            )
        }
        IconButton(onClick = {navController.navigate("noti")}) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "",
                modifier = Modifier.size(50.dp),
                tint = Color(0xFFFFC107)
            )
        }
    }
}

@Composable
fun thongtinsinhvien(email: String?, name: String?, cccd: String?) {
    tieude("Thông tin sinh viên")
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp, vertical = 15.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xCCADD8E6)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp),
        ) {
            itemThongtin(Icons.Default.Email, "Email", email)
            itemThongtin(Icons.Default.PermIdentity, "Họ và tên", name)
            itemThongtin(Icons.Default.School, "cccd", cccd)

        }
    }
}

// cccd: Badget, hovaten: PermIdentity, mssv: School
@Composable
fun itemThongtin(icon: ImageVector?, title: String, content: String?) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let {
            Icon(
                imageVector = icon,
                contentDescription = ""
            )
        }
        Spacer(modifier = Modifier.width(5.dp))
        //0xFF333333
        Text(
            text = "${title}: ",
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333),
            fontSize = 19.sp
        )
        Text(
            text = content ?: "Đang tải ....",
            fontSize = 19.sp,
            color = Color(0xFF555555)
        )
    }
}

@Composable
fun tieude(title: String) {
    Spacer(modifier = Modifier.height(50.dp))
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 25.sp,
        color = Color.Red,
        modifier = Modifier.padding(horizontal = 20.dp)
    )
}
@Composable
fun thongtinxe(biensoxe: String?, trangthai: Boolean?, firebaseViewModel: FirebaseViewModel) {
    tieude("Thông tin xe")
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp, vertical = 15.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xCCADD8E6)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp),
        ) {
            itemThongtin(null,"Biển số xe", biensoxe)
            itemTrangthai("Trạng thái", trangthai)
            buttonLeave(
                isTrangThai = trangthai,
                firebaseViewModel = firebaseViewModel,
                bienSo = biensoxe
            )
        }
    }
}
@Composable
fun thongtinxePhu(
    biensophu: String?,
    trangthaiPhu: Boolean?,
    ngayHetHan: String?,
    firebaseViewModel: FirebaseViewModel,
    userViewModel: UserViewModel)
{
    val ngayHetHanMillis = remember(ngayHetHan) {
        try {
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            format.parse(ngayHetHan)?.time
        } catch (e: Exception) {
            null
        }
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp, vertical = 15.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xCCADD8E6)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp),
        ) {
            itemThongtin(null, "Biển số lạ", biensophu)
            itemTrangthai("Trạng thái", trangthaiPhu)
            buttonLeave(
                isTrangThai = trangthaiPhu,
                firebaseViewModel = firebaseViewModel,
                bienSo = biensophu,
                isPhu = true
            )
            // Hiển thị ngày hết hạn
            if (!ngayHetHan.isNullOrBlank()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    ngayHetHan.let {
                        Text(
                            text = "Ngày hết hạn: $it",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 19.sp
                        )
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                buttonGiaHanPhu(
                    isTrangThaiPhu = trangthaiPhu,
                    bienSoPhu = biensophu,
                    ngayHetHanStr = ngayHetHan,
                    userViewModel = userViewModel
                )
                buttonDeletePhu(biensophu, userViewModel)
            }
        }
    }
}

@Composable
fun itemTrangthai(title: String, trangthai: Boolean?) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = "${title}: ",
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333),
            fontSize = 19.sp
        )
        Spacer(modifier = Modifier.width(5.dp))
        Icon(
            imageVector = Icons.Default.Adjust,
            contentDescription = "",
            tint = when(trangthai){
                false -> Color(0xFFFFC107)
                true -> Color(0xFF4CAF50)
                else -> Color(0xFF555555)
            },
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = when(trangthai){
                false -> "Chuẩn bị rời"
                true -> "Đang đổ"
                else -> "Chưa đổ"
            },
            color = when(trangthai){
                false -> Color(0xFFFFC107)
                true -> Color(0xFF4CAF50)
                else -> Color(0xFF555555)
            },
            fontSize = 19.sp
        )
    }
}

//0xFFD30101
@Composable
fun buttonLeave(
    isTrangThai: Boolean?,
    firebaseViewModel: FirebaseViewModel,
    bienSo: String?,
    isPhu: Boolean = false // Mặc định là xe chính
) {
    Button(
        onClick = {
            bienSo?.let {
                if (isPhu)
                    firebaseViewModel.updateCarTrangthaiPhu(it, false)
                else
                    firebaseViewModel.updateCarTrangthai(it, false)
            }
        },
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = when (isTrangThai) {
                true -> Color(0xFFD30101)
                else -> Color(0xFF555555)
            }
        ),
        modifier = Modifier
            .width(200.dp)
            .height(55.dp),
        enabled = isTrangThai ?: false
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Chuẩn bị rời",
                fontWeight = FontWeight.Bold,
                color = Color(0xF2FFFFFF),
                fontSize = 21.sp
            )
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = "",
                tint = Color(0xF2FFFFFF),
                modifier = Modifier.size(60.dp)
            )
        }
    }
}

@Composable
fun ThemXePhuButton(userViewModel: UserViewModel) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        ThemXePhuDialog(userViewModel = userViewModel)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(y = (-80).dp)
        ) {
            Text("Thêm xe lạ")
        }
    }
}

@Composable
fun buttonDeletePhu(biensophu: String?, userViewModel: UserViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    Button(
        onClick = {
            showDialog = true
        },
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Red
        ),
        modifier = Modifier
            .width(130.dp)
            .height(55.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Xoá",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 21.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Xoá biển số phụ",
                modifier = Modifier
                    .size(28.dp),
                tint = Color.White,
            )
        }
    }
    if (showDialog) {
        ConfirmDeleteDialog(
            biensophu = biensophu,
            userViewModel = userViewModel,
            onDismiss = { showDialog = false }
        )
    }
}
@Composable
fun buttonGiaHanPhu(
    isTrangThaiPhu: Boolean?,
    bienSoPhu: String?,
    ngayHetHanStr: String?,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    val message = userViewModel.giaHanMessage

    // Hiển thị Toast khi có message từ ViewModel
    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            userViewModel.clearGiaHanMessage()
        }
    }

    // Tính toán thời gian hết hạn từ chuỗi
    val ngayHetHanMillis = try {
        ngayHetHanStr?.let {
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            format.parse(it)?.time
        }
    } catch (e: Exception) {
        null
    }

    val currentTime = System.currentTimeMillis()
    val MILI_GIO = 60 * 60 * 1000
    val thoiGianConLai = (ngayHetHanMillis ?: 0L) - currentTime
    val isEnabled = isTrangThaiPhu == false && thoiGianConLai in 1..(12 * MILI_GIO)

    Button(
        onClick = {
            bienSoPhu?.let {
                userViewModel.giaHanBienSoPhu(it)
            }
        },
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isEnabled) Color(0xFFFFC107) else Color(0xFFBDBDBD)
        ),
        enabled = isEnabled,
        modifier = Modifier
            .width(200.dp)
            .height(55.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Gia hạn",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = "Gia hạn",
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

fun Long.toDateString(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(this))
}
