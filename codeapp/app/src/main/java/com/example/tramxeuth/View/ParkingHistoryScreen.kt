package com.example.tramxeuth.View

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Input
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.tramxeuth.Model.ParkingRecord
import com.example.tramxeuth.ViewModel.ParkingHistoryViewModel
import com.example.tramxeuth.ViewModel.convertDate

@Composable
fun ParkingHistoryScreen(
    navHostController: NavHostController,
    parkingHistoryViewModel: ParkingHistoryViewModel,
    biensoxe: String
) {
    val listParkingHistory by parkingHistoryViewModel.listParkingHistory.collectAsState()
    LaunchedEffect(Unit) {
        parkingHistoryViewModel.getParkingHistoryById(biensoxe)
    }
    Column (
        modifier = Modifier
            .fillMaxSize()
    ) {
        TopLayout(navHostController, "Lịch sử gửi xe")

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                listParkingHistory?.forEach{ parkingRecord ->
                    item {
                        HistoryChild(parkingRecord = parkingRecord, onClick = {navHostController.navigate("detail_parkingHistory/${parkingRecord.date}")})
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryChild(
    parkingRecord: ParkingRecord,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xCCADD8E6)),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Divider(thickness = 2.dp)
        Row(
            modifier = Modifier
//                .background()
                .padding(vertical = 15.dp, horizontal = 12.dp)
                .fillMaxWidth()
                .clickable(){
                    onClick()
                },
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = "Ngày gửi: ${parkingRecord.date?.let { convertDate(it) }}",
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = "",
                        modifier = Modifier.size(12.dp),
                        tint =  MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Số lần gửi xe trong hôm nay: ${parkingRecord.vehicles!!.first { it.licensePlate != null }.totalIn}",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}