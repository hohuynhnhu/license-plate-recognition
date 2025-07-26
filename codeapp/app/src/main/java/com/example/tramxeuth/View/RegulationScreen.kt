package com.example.tramxeuth.View

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tramxeuth.Model.QuyDinh
import com.example.tramxeuth.ViewModel.RegulationViewModel
import kotlinx.coroutines.flow.StateFlow

@Composable
fun RegulationScreen(navController: NavController, regulationViewModel: RegulationViewModel) {
    val regulationState = regulationViewModel.regulation.collectAsState()

    LaunchedEffect(Unit) {
        regulationViewModel.loadRegulation()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopBar(navController)
        Spacer(modifier = Modifier.height(24.dp))

        if (regulationState.value != null) {
            RegulationCard(regulationState.value!!)
        } else {
            // Bạn có thể thay bằng CircularProgressIndicator hoặc Text "Đang tải..."
            Text(
                text = "Đang tải quy định...",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}


@Composable
fun TopBar(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = "Back",
                tint = Color(0xFF333333)
            )
        }

        Text(
            text = "Quy định",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333),
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun RegulationCard(regulation: QuyDinh) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Nội dung quy định",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A237E) // Dark indigo
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = regulation.ghichu,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                color = Color(0xFF424242)
            )
        }
    }
}

