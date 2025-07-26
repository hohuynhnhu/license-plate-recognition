package com.example.tramxeuth.View

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.tramxeuth.Model.ParkingRecord
import com.example.tramxeuth.Model.TimeLine
import com.example.tramxeuth.ViewModel.FirebaseViewModel
import com.example.tramxeuth.ViewModel.ParkingHistoryViewModel

@Composable
fun DetailParkingScreen(
    parkingHistoryViewModel: ParkingHistoryViewModel,
    date: String,
    navHostController: NavHostController,
) {
    val listParkingHistory by parkingHistoryViewModel.listParkingHistory.collectAsState()

    var showMediaDetail by remember { mutableStateOf(false) }
    var selectedImage by remember { mutableStateOf<String?>(null) }
    if (showMediaDetail && selectedImage != null) {
        MediaDetailDialog(
            image = selectedImage?:"",
            onDismiss = { showMediaDetail = false }
        )
    }
    Column (
        modifier = Modifier
            .fillMaxSize()
    ) {
        TopLayout(navHostController, "Lịch sử gửi xe")

        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            val parkingHistory = listParkingHistory?.first { it.date == date }
            item { Text(
                text = "Ngày gửi: $date",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )  }
            parkingHistory?.vehicles!!.first { it.licensePlate != null }.timelines?.forEachIndexed { index, timeLine ->
                item { Text(
                    text = "Lần ${index + 1}:",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                ) }
                item { DetailParkingChild(timeLine, onClickImage = {
                    selectedImage = it
                    showMediaDetail = true
                }) }
            }
        }
    }
}

@Composable
fun DetailParkingChild(
    timeLine: TimeLine,
    onClickImage: (String?) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 5.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xCCADD8E6)
        )
    ) {
        Column(
            modifier = Modifier.padding(5.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier.fillMaxWidth(0.5f)
                ) {
                    Text("Thời gian vào: ${timeLine.timeIn}")
                    AsyncImage(
                        model = timeLine.imageIn,
                        contentDescription = null,
                        modifier = Modifier
                            .width(50.dp)
                            .height(50.dp)
                            .clickable { onClickImage(timeLine.imageIn) }
                    )
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier.fillMaxWidth(0.5f)
                ) {
                    Text("Thời gian ra: ${timeLine.timeOut}")
                    AsyncImage(
                        model = timeLine.imageOut,
                        contentDescription = null,
                        modifier = Modifier
                            .width(50.dp)
                            .height(50.dp)
                            .clickable { onClickImage(timeLine.imageOut) }
                    )
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
        IconButton(onClick = {
            navHostController.popBackStack()
        }
        ) {
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