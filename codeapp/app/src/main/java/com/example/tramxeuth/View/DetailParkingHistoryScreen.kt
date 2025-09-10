package com.example.tramxeuth.View

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.tramxeuth.Model.*

@Composable
fun DetailParkingScreenMotorbike(
    record: ParkingRecordMotorbike?,
    navHostController: NavHostController
) {
    var showMediaDetail by remember { mutableStateOf(false) }
    var selectedImage by remember { mutableStateOf<String?>(null) }

    if (showMediaDetail && selectedImage != null) {
        MediaDetailDialog(
            image = selectedImage ?: "",
            onDismiss = { showMediaDetail = false }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopLayout(navHostController, "Lịch sử gửi xe máy")

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Text(
                    text = "Ngày gửi: ${record?.date ?: "Không có"}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(10.dp)
                )
            }

            record?.vehicles?.forEach { vehicle ->
                vehicle.timelines.forEachIndexed { index, timeline ->
                    item {
                        Text(
                            text = "Xe ${vehicle.licensePlate} - Lần ${index + 1}:",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 12.dp, top = 5.dp)
                        )
                    }

                    item {
                        DetailParkingChildXeMay(
                            timeLine = timeline,
                            onClickImage = {
                                selectedImage = it
                                showMediaDetail = true
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailParkingScreenCar(
    record: ParkingRecordCar?,
    navHostController: NavHostController
) {
    var showMediaDetail by remember { mutableStateOf(false) }
    var selectedImage by remember { mutableStateOf<String?>(null) }

    if (showMediaDetail && selectedImage != null) {
        MediaDetailDialog(
            image = selectedImage ?: "",
            onDismiss = { showMediaDetail = false }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopLayout(navHostController, "Lịch sử gửi ô tô")

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Text(
                    text = "Ngày gửi: ${record?.date ?: "Không có"}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(10.dp)
                )
            }
            record?.vehicles?.forEach { vehicle ->
                Log.d("Dữ liệu record: ",record.toString())
                vehicle.timelines.forEachIndexed { index, timeline ->
                    item {
                        Text(
                            text = "Xe ${vehicle.licensePlate} - Lần ${index + 1}:",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 12.dp, top = 5.dp)
                        )
                    }

                    item {
                        DetailParkingChildXeOto(
                            timeLine = timeline,
                            onClickImage = {
                                selectedImage = it
                                showMediaDetail = true
                            }
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun DetailParkingChildXeMay(
    timeLine: TimeLineXeMay,
    onClickImage: (String?) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 5.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xCCADD8E6))
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Cột 1: Thời gian vào + khuôn mặt vào
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Thời gian vào: ${timeLine.timein ?: "Chưa có"}")
                    AsyncImage(
                        model = timeLine.khuonmatvao,
                        contentDescription = "Ảnh khuôn mặt vào",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onClickImage(timeLine.khuonmatvao) }
                    )
                }

                // Cột 2: Thời gian ra + khuôn mặt ra
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Thời gian ra: ${timeLine.timeout ?: "Chưa có"}")
                    AsyncImage(
                        model = timeLine.khuonmatra,
                        contentDescription = "Ảnh khuôn mặt ra",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onClickImage(timeLine.khuonmatra) }
                    )
                }


            }
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Cột 3: Biển số vào/ra
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("Biển số vào:")
                        AsyncImage(
                            model = timeLine.biensoxevao,
                            contentDescription = "Ảnh biển số vào",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable{onClickImage(timeLine.biensoxevao)}
                        )

                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("Biển số ra:")
                        AsyncImage(
                            model = timeLine.biensoxera,
                            contentDescription = "Ảnh biển số ra",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onClickImage(timeLine.biensoxera) }
                    )
                         }
                }
        }
    }
}
@Composable
fun DetailParkingChildXeOto(
    timeLine: TimeLineXeOto,
    onClickImage: (String?) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 5.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD1F0C9))
    ) {
        Column(modifier = Modifier.padding(8.dp)) {

            // Row 1: Thời gian vào / ra
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Thời gian vào: ${timeLine.timein ?: "Chưa có"}")
                    timeLine.hinhxevao?.let { url ->
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                                .clickable { onClickImage(url) }
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Thời gian ra: ${timeLine.timeout ?: "Chưa có"}")
                    timeLine.hinhxera?.let { url ->
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                                .clickable { onClickImage(url) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Row 2: Biển số vào / ra
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Biển số vào:")
                    timeLine.biensoxevao?.let { url ->
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                                .clickable { onClickImage(url) }
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Biển số ra:")
                    timeLine.biensoxera?.let { url ->
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                                .clickable { onClickImage(url) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Row 3: Logo xe vào / ra
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Logo xe vào:")
                    timeLine.logovao?.forEach { url ->
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                                .clickable { onClickImage(url) }
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Logo xe ra:")
                    timeLine.logora?.forEach { url ->
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                                .clickable { onClickImage(url) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TopLayout(
    navHostController: NavHostController,
    title: String,
    space: Dp = 40.dp,
) {
    Spacer(modifier = Modifier.height(30.dp))
    Row(
        modifier = Modifier
            .padding(horizontal = 13.dp, vertical = 10.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { navHostController.popBackStack() }) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "Quay lại",
                tint = Color.Black,
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(modifier = Modifier.width(space))
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}
