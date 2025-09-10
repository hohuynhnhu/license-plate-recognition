package com.example.tramxeuth.View

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TagFaces
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tramxeuth.R
import com.example.tramxeuth.ViewModel.AuthViewModel
import com.example.tramxeuth.ViewModel.FirebaseViewModel
import com.example.tramxeuth.ViewModel.NotificationViewModel
import com.example.tramxeuth.ViewModel.PaymentViewModel
import com.example.tramxeuth.ViewModel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun homeScreen(navController: NavController, authViewModel: AuthViewModel, userViewModel: UserViewModel, firebaseViewModel: FirebaseViewModel) {
    val user = userViewModel.currentUser
    val paymentViewModel: PaymentViewModel = viewModel()
    val url by paymentViewModel.urlPayment.collectAsState()

    val isTrangthai = firebaseViewModel.isTrangthai.value
    val isTrangthaiPhu = firebaseViewModel.isTrangthaiPhu.value
    LaunchedEffect(url) {
        url?.let {
            val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
            navController.navigate("payment_web/$encodedUrl")
        }
    }

    LaunchedEffect(user) {
        if (user == null)
            userViewModel.loadUserData()
        else{
            firebaseViewModel.startListeningTrangthai(user.biensoxe)
            firebaseViewModel.startListeningCanhbao(user.biensoxe)
            firebaseViewModel.startListeningAutoLeave(user.biensoxe)

            user.biensophu?.bienSo?.let { bienSoPhu ->
                firebaseViewModel.startListeningTrangthaiPhu(bienSoPhu)
                firebaseViewModel.startListeningCanhbaoPhu(bienSoPhu)
                firebaseViewModel.startListeningAutoLeavePhu(bienSoPhu)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
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
            topLayout(navController, paymentViewModel, user?.ten, user?.luot)
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
            onClick = {
                authViewModel.logout {
                    userViewModel.clearUserData()
                    navController.navigate("login")
                }
            },
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier
                .size(56.dp)
                .align(Alignment.BottomEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = "Đăng xuất",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }

    }
}

@Composable
fun topLayout(
    navController: NavController,
    paymentViewModel: PaymentViewModel,
    ten: String?,
    luot: Int?
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedQuantity by remember { mutableStateOf(0) }
    val cost = 5000
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 50.dp, horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
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
        Spacer(modifier = Modifier.width(5.dp))
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xB3FFFFFF)
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(horizontal = 7.dp, vertical = 7.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ConfirmationNumber,
                    contentDescription = "",
                    modifier = Modifier.size(25.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                luot?.let{
                    Text(
                        text = "Lượt: $luot",
                        fontSize = 17.sp,
                    )
                }
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = "add",
                    modifier = Modifier
                        .size(17.dp)
                        .clickable { showDialog = true }
                )
            }

        }
        IconButton(onClick = { navController.navigate("regulation") }) {
            Icon(
                imageVector = Icons.Default.Quiz,
                contentDescription = "",
                modifier = Modifier.size(40.dp),
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
    QuantityScreen(
        showDialog = showDialog,
        onDismiss = { showDialog = false },
        onConfirm = {
            qty -> selectedQuantity = qty
            paymentViewModel.createPaymentUrl(selectedQuantity*cost, selectedQuantity)
            Log.d("createPaymentUrl", "đang tạo PaymentUrl")
        }
    )
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
            itemThongtin(Icons.Default.School, "CCCD", cccd)

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
            // Auto leave checkbox
            autoLeaveCheckbox(
                firebaseViewModel = firebaseViewModel,
                bienSo = biensoxe,
                isPhu = false
            )
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
            // Auto leave checkbox for secondary car
            autoLeaveCheckbox(
                firebaseViewModel = firebaseViewModel,
                bienSo = biensophu,
                isPhu = true
            )
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
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                buttonGiaHanPhu(
                    bienSoPhu = biensophu,
                    userViewModel = userViewModel,
                    Modifier.weight(1f)
                )
                buttonDeletePhu(biensophu, userViewModel, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun autoLeaveCheckbox(
    firebaseViewModel: FirebaseViewModel,
    bienSo: String?,
    isPhu: Boolean = false
) {
    val autoLeaveEnabled = if (isPhu) {
        firebaseViewModel.autoLeaveEnabledPhu.value
    } else {
        firebaseViewModel.autoLeaveEnabled.value
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(
                width = 1.dp,
                color = Color(0xFFBBBBBB),
                shape = RoundedCornerShape(8.dp),
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (autoLeaveEnabled) Color(0x1A4CAF50) else Color(0x1AFFFFFF)
        ),
//        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        bienSo?.let { bienSoValue ->
                            if (isPhu) {
                                firebaseViewModel.setAutoLeavePhu(bienSoValue, !autoLeaveEnabled)
                            } else {
                                firebaseViewModel.setAutoLeave(bienSoValue, !autoLeaveEnabled)
                            }
                        }
                    }
            ) {
                Checkbox(
                    checked = autoLeaveEnabled,
                    onCheckedChange = { isChecked ->
                        bienSo?.let { bienSoValue ->
                            if (isPhu) {
                                firebaseViewModel.setAutoLeavePhu(bienSoValue, isChecked)
                            } else {
                                firebaseViewModel.setAutoLeave(bienSoValue, isChecked)
                            }
                        }
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF4CAF50),
                        uncheckedColor = Color(0xFF999999),
                        checkmarkColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tự động chuẩn bị rời",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF333333)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Status indicator
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = if (autoLeaveEnabled) Color(0xFF4CAF50) else Color(0xFFBDBDBD),
                                    shape = CircleShape
                                )
                        )
                    }

                    // Always show description for better UX
                    Text(
                        text = if (autoLeaveEnabled) {
                            "✓ Đang bật - Xe sẽ tự động chuyển sang trạng thái 'chuẩn bị rời'"
                        } else {
                            "Bật để tự động kích hoạt 'chuẩn bị rời' khi đỗ xe"
                        },
                        fontSize = 14.sp,
                        color = if (autoLeaveEnabled) Color(0xFF4CAF50) else Color(0xFF777777),
                        modifier = Modifier.padding(top = 4.dp),
                        lineHeight = 18.sp
                    )
                }
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
            .fillMaxWidth()
            .heightIn(min = 55.dp, max = 60.dp),
        enabled = isTrangThai ?: false
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Chuẩn bị rời",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                fontSize = 21.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = "",
                tint = Color(0xF2FFFFFF),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun ThemXePhuButton(userViewModel: UserViewModel) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        ThemXePhuDialog(
            userViewModel = userViewModel,
            showDialog = showDialog,
            onDismiss = { showDialog = false }
        )
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
fun buttonDeletePhu(biensophu: String?, userViewModel: UserViewModel, modifier: Modifier = Modifier) {
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val thongBao = userViewModel.thongBaoXoaPhu
    LaunchedEffect(thongBao) {
        thongBao?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            userViewModel.clearThongBaoXoaPhu()
        }
    }

    Button(
        onClick = {
            if (!biensophu.isNullOrBlank()) {
                showDialog = true
            } else {
                Toast.makeText(context, "Biển số lạ không hợp lệ", Toast.LENGTH_SHORT).show()
            }
        },
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
        modifier = modifier.heightIn(min = 55.dp, max = 60.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Xoá", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 21.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Xoá biển số phụ",
                modifier = Modifier.size(28.dp),
                tint = Color.White
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
    bienSoPhu: String?,
    userViewModel: UserViewModel,
    modifier: Modifier = Modifier
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

    val isEnabled = true

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
        modifier = modifier.heightIn(min = 55.dp, max = 60.dp)
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

@Composable
fun QuantityScreen(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var quantity by remember { mutableStateOf(1) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text("Chọn số lượng") },
            text = {
                QuantityPicker(
                    quantity = quantity,
                    onQuantityChange = { newQty -> quantity = newQty }
                )
            },
            confirmButton = {
                Button(onClick = {
                    onConfirm(quantity)
                    onDismiss()
                }) {
                    Text("Xác nhận")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { onDismiss() }) {
                    Text("Hủy")
                }
            }
        )
    }
}

@Composable
fun QuantityPicker(
    quantity: Int,
    onQuantityChange: (Int) -> Unit
) {
    var textValue by remember { mutableStateOf(quantity.toString()) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Nút giảm
        Icon(
            imageVector = Icons.Default.RemoveCircle,
            contentDescription = "Remove",
            modifier = Modifier
                .size(30.dp)
                .clickable {
                    if (quantity > 1) {
                        val newQty = quantity - 1
                        textValue = newQty.toString()
                        onQuantityChange(newQty)
                    }
                }
        )

        Spacer(modifier = Modifier.width(16.dp))

        // TextField nhập trực tiếp
        OutlinedTextField(
            value = textValue,
            onValueChange = { newText ->
                textValue = newText
                val newQty = newText.toIntOrNull()
                if (newQty != null && newQty >= 1) {
                    onQuantityChange(newQty)
                }
            },
            singleLine = true,
            modifier = Modifier.width(80.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Nút tăng
        Icon(
            imageVector = Icons.Default.AddCircle,
            contentDescription = "Add",
            modifier = Modifier
                .size(30.dp)
                .clickable {
                    val newQty = quantity + 1
                    textValue = newQty.toString()
                    onQuantityChange(newQty)
                }
        )
    }
}