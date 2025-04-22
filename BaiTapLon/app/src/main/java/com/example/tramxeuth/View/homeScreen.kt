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
import androidx.compose.material.icons.filled.Badge
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tramxeuth.Model.thongtinSinhvien
import com.example.tramxeuth.R
import kotlin.math.sinh

@Composable
fun homeScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize()
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
        var sinhvien1 = thongtinSinhvien("123456789012", "Nguyễn Thành Đạt", "2251120413")
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 150.dp),
            shape = RoundedCornerShape(topEnd = 30.dp, topStart = 30.dp)
        ) {
            thongtinsinhvien(sinhvien1.cccd, sinhvien1.name, sinhvien1.mssv)
            thongtinxe()
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
fun thongtinsinhvien(cccd: String, name: String, mssv: String) {
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
            itemThongtin(Icons.Default.Badge, "CCCD", cccd)
            itemThongtin(Icons.Default.PermIdentity, "Họ và tên", name)
            itemThongtin(Icons.Default.School, "MSSV", mssv)

        }
    }
}

// cccd: Badget, hovaten: PermIdentity, mssv: School
@Composable
fun itemThongtin(icon: ImageVector?, title: String, content: String) {
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
            text = content,
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
fun thongtinxe() {
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
            itemThongtin(null,"Biển số xe", "A1-11 11111")
            itemTrangthai("Trạng thái", 0xFF555555)
            buttonLeave(0xFFD30101, true)
        }
    }
}

@Composable
fun itemTrangthai(title: String, colorIcon: Long,) {
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
            tint = Color(colorIcon)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = "Chưa đổ",
            color = Color(0xFF555555),
            fontSize = 19.sp
        )
    }
}

//0xFFD30101
@Composable
fun buttonLeave(colorButton: Long, state: Boolean) {
    Button(
        onClick = {},
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(colorButton)
        ),
        modifier = Modifier.width(200.dp)
            .height(55.dp),
        enabled = state
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