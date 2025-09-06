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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChiTietXeScreen(navController: NavController, ngayId: String, collection: String, xeId: String) {
    val db = FirebaseFirestore.getInstance()

    var solanra by remember { mutableStateOf<Int?>(null) }
    var solanvao by remember { mutableStateOf<Int?>(null) }
    var timelineList by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    suspend fun loadData() {
        try {
            isLoading = true
            errorMessage = null

            // Lấy thông tin xe
            val docRef = db.collection("lichsuhoatdong")
                .document(ngayId)
                .collection(collection)
                .document(xeId)
                .get()
                .await()

            solanra = docRef.getLong("solanra")?.toInt()
            solanvao = docRef.getLong("solanvao")?.toInt()

            // Lấy timeline
            val timelineSnapshot = db.collection("lichsuhoatdong")
                .document(ngayId)
                .collection(collection)
                .document(xeId)
                .collection("timeline")
                .get()
                .await()

            timelineList = timelineSnapshot.documents.map { it.id }

        } catch (e: Exception) {
            errorMessage = "Lỗi: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết xe $xeId") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
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
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(errorMessage ?: "", color = Color.Red)
                    }
                }

                solanra == null && solanvao == null && timelineList.isEmpty() -> {
                    //  Không có dữ liệu gì cả
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📭", fontSize = 40.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Không có dữ liệu", color = Color.Gray)
                        }
                    }
                }

                else -> {
                    //  Có dữ liệu thì hiển thị
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Thông tin số lần ra/vào (chỉ hiển thị nếu có)
                        if (solanra != null || solanvao != null) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(8.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(Modifier.padding(16.dp)) {
                                        Text("Thông tin:", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                        Spacer(Modifier.height(8.dp))
                                        Text(" Số lần vào: ${solanvao ?: 0}", fontSize = 18.sp)
                                        Text(" Số lần ra: ${solanra ?: 0}", fontSize = 18.sp)
                                    }
                                }
                            }
                        }

                        // Timeline (chỉ hiển thị nếu có)
                        if (timelineList.isNotEmpty()) {
                            item {
                                Text(
                                    "⏳ Timeline",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00AEFF)
                                )
                            }
                            items(timelineList) { timelineId ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                        navController.navigate("chitiettimeline/$ngayId/$collection/$xeId/$timelineId")
                                    },
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(8.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Text(
                                        text = timelineId,
                                        modifier = Modifier.padding(16.dp),
                                        fontSize = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
