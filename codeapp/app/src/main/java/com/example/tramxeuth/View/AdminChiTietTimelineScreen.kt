package com.example.tramxeuth.View

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tramxeuth.ViewModel.ChiTietTimelineViewModel
import com.example.tramxeuth.common.ZoomableImage
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChiTietTimelineScreen(
    navController: NavController,
    ngayId: String,
    collection: String,
    xeId: String,
    timelineId: String
) {
    val viewModel: ChiTietTimelineViewModel = viewModel()
    val item = viewModel.item
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage
    var selectedImage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadTimelineDetail(ngayId, collection, xeId, timelineId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết $timelineId") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF00AEFF),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                errorMessage != null -> Text("Lỗi: $errorMessage", Modifier.align(Alignment.Center))
                item == null -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("📭", fontSize = 40.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("Không có dữ liệu", color = Color.Gray)
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Thời gian
                        item {
                            Text("⏰ Thời gian", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Spacer(Modifier.height(8.dp))
                            Text("Time In: ${item.timein ?: "-"}", fontSize = 18.sp)
                            Text("Time Out: ${item.timeout ?: "-"}", fontSize = 18.sp)
                        }

                        when (collection) {
                            "xeoto" -> {
                                // Biển số
                                item {
                                    Text(" Biển số", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                    Spacer(Modifier.height(8.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        item.biensoxevao?.let {
                                            AsyncImage(
                                                model = it,
                                                contentDescription = "Biển số vào",
                                                modifier = Modifier
                                                    .size(150.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .clickable { selectedImage = it },
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                        item.biensoxera?.let {
                                            AsyncImage(
                                                model = it,
                                                contentDescription = "Biển số ra",
                                                modifier = Modifier
                                                    .size(150.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .clickable { selectedImage = it },
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                    }
                                }

                                // Hình xe
                                item {
                                    Text(" Hình xe", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                    Spacer(Modifier.height(8.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        item.hinhxevao?.let {
                                            AsyncImage(
                                                model = it,
                                                contentDescription = "Xe vào",
                                                modifier = Modifier
                                                    .size(150.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .clickable { selectedImage = it },
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                        item.hinhxera?.let {
                                            AsyncImage(
                                                model = it,
                                                contentDescription = "Xe ra",
                                                modifier = Modifier
                                                    .size(150.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .clickable { selectedImage = it },
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                    }
                                }

                                // Logo
                                item {
                                    Text("🏷 Logo", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                    Spacer(Modifier.height(8.dp))
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        item.logovao?.forEach { url ->
                                            AsyncImage(
                                                model = url,
                                                contentDescription = "Logo vào",
                                                modifier = Modifier
                                                    .size(100.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .clickable { selectedImage = url },
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                        item.logora?.forEach { url ->
                                            AsyncImage(
                                                model = url,
                                                contentDescription = "Logo ra",
                                                modifier = Modifier
                                                    .size(100.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .clickable { selectedImage = url },
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                    }
                                }
                            }

                            "xe", "xemay" -> {
                                // Biển số
                                item {
                                    Text("📌 Biển số", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                    Spacer(Modifier.height(8.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        item.biensoxevao?.let {
                                            AsyncImage(
                                                model = it,
                                                contentDescription = "Biển số vào",
                                                modifier = Modifier
                                                    .size(150.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .clickable { selectedImage = it },
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                        item.biensoxera?.let {
                                            AsyncImage(
                                                model = it,
                                                contentDescription = "Biển số ra",
                                                modifier = Modifier
                                                    .size(150.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .clickable { selectedImage = it },
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                    }
                                }

                                // Khuôn mặt
                                item {
                                    Text("🙂 Khuôn mặt", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                    Spacer(Modifier.height(8.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        item.khuonmatvao?.let {
                                            AsyncImage(
                                                model = it,
                                                contentDescription = "Khuôn mặt vào",
                                                modifier = Modifier
                                                    .size(150.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .clickable { selectedImage = it },
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                        item.khuonmatra?.let {
                                            AsyncImage(
                                                model = it,
                                                contentDescription = "Khuôn mặt ra",
                                                modifier = Modifier
                                                    .size(150.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .clickable { selectedImage = it },
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Hiển thị ảnh phóng to toàn màn hình
                    ZoomableImage(imageUrl = selectedImage) {
                        selectedImage = null
                    }
                }
            }
        }
    }
}
