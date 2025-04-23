package com.example.tramxeuth.View

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tramxeuth.R
import com.example.tramxeuth.ViewModel.AuthViewModel
import com.example.tramxeuth.ViewModel.FirebaseViewModel
import com.example.tramxeuth.ViewModel.UserViewModel

@Composable
fun homeScreen(navController: NavController, authViewModel: AuthViewModel, userViewModel: UserViewModel, firebaseViewModel: FirebaseViewModel) {
    val user = userViewModel.currentUser
    val isActive = firebaseViewModel.isActive.value

    LaunchedEffect(user) {
        if (user == null)
            userViewModel.loadUserData()
        else
            user.biensoxe?.let { firebaseViewModel.startListening(it) }
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
            topLayout(navController)
        }
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 150.dp),
            shape = RoundedCornerShape(topEnd = 30.dp, topStart = 30.dp)
        ) {
            thongtinsinhvien(user?.email, user?.ten, user?.mssv)
            thongtinxe(user?.biensoxe, isActive, firebaseViewModel)
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
fun topLayout(navController: NavController) {
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
                    Text(
                        text = "Nguyễn Thành Đạt",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF003153)
                    )
                }
            }

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
fun thongtinsinhvien(email: String?, name: String?, mssv: String?) {
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
            itemThongtin(Icons.Default.School, "MSSV", mssv)

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
            buttonLeave( trangthai, firebaseViewModel, biensoxe)
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
fun buttonLeave( trangthai: Boolean?, firebaseViewModel: FirebaseViewModel, biensoxe: String?) {
    Button(
        onClick = {
            if (biensoxe != null) {
                firebaseViewModel.updateCar(biensoxe, false)
            }
        },
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = when(trangthai){
                true -> Color(0xFFD30101)
                else -> Color(0xFF555555)
            }
        ),
        modifier = Modifier
            .width(200.dp)
            .height(55.dp),
        enabled = trangthai?: false
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