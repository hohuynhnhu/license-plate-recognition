package com.example.tramxeuth.View

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tramxeuth.R

@Composable
fun SignForm(title: String, navController: NavController){
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_signform),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Card(
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xCC003153)
            ),
            shape = RoundedCornerShape(0.dp)
        ){}
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(top = 70.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_ngang_uth),
                contentDescription = "",
                modifier = Modifier.fillMaxWidth()
                    .size(70.dp)
            )
        }
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(top = 180.dp, start = 30.dp, end = 30.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFFFD700),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(30.dp))
            itemSection("Họ và tên", Icons.Default.AccountCircle)
            itemSection("Mật khẩu", Icons.Default.Lock)
            Spacer(modifier = Modifier.height(5.dp))
            if (title == "Đăng nhập"){
                Text(
                    text = "Đăng ký",
                    textDecoration = TextDecoration.Underline,
                    fontSize = 17.sp,
                    color = Color(0xFF00AEFF),
                    modifier = Modifier.clickable {
                        navController.navigate("logup")
                    }
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
            Button(
                onClick = {
                    if(title == "Đăng nhập"){
                        navController.navigate("home")
                    }
                    else if (title == "Đăng ký"){
                        navController.navigate("login")
                    }
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(200.dp)
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00AEFF)
                )
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp
                )
            }
        }
    }
}

@Composable
fun itemSection(title: String, icon: ImageVector, ){
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "",
                tint = Color(0xFFB3FFF3),
                )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = title,
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(7.dp))
        var text by remember{(mutableStateOf(""))}
        var isPasswordVisible by remember { mutableStateOf(false) }
        OutlinedTextField(
            value = "",
            onValueChange = {text = it},
            placeholder = { Text(title) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
                .background(Color(0xB3FFFFFF), shape = RoundedCornerShape(8.dp)),
            visualTransformation = if (isPasswordVisible && title == "Mật khẩu") VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                if (title == "Mật khẩu"){
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null
                        )
                    }
                }
            }
        )
        Spacer(modifier = Modifier.height(20.dp))
    }
}

