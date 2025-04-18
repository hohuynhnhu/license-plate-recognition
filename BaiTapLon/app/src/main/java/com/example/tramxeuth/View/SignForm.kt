package com.example.tramxeuth.View

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tramxeuth.R

@Composable
fun SignForm(title: String){
    Box(
        modifier = Modifier.fillMaxSize()
            .padding(vertical = 40.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.bgdangnhap),
            contentDescription = "",
            contentScale = ContentScale.FillBounds, // hoặc .Fit tùy nhu cầu
            modifier = Modifier.width(400.dp)
                .height(300.dp)
        )
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = 30.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            itemSection("Họ và tên")
            itemSection("Mật khẩu")
            Button(
                onClick = {},
                modifier = Modifier.align(Alignment.CenterHorizontally)){
                Text(title)
            }
        }
    }
}

@Composable
fun itemSection(title: String){
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(title)
        Spacer(modifier = Modifier.height(7.dp))
        var text by remember{(mutableStateOf(""))}
        var isPasswordVisible by remember { mutableStateOf(false) }
        OutlinedTextField(
            value = "",
            onValueChange = {text = it},
            placeholder = { Text(title) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (isPasswordVisible && title == "Mật khẩu") VisualTransformation.None else PasswordVisualTransformation(),
        )
        Spacer(modifier = Modifier.height(20.dp))
    }
}

