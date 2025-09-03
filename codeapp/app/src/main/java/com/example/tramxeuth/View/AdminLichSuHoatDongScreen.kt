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
// Hàm chuyển đổi docId (ddmmyyyy) thành định dạng ngày
fun formatDocIdToDate(docId: String): String {
    return try {
        if (docId.length == 8 && docId.all { it.isDigit() }) {
            val day = docId.substring(0, 2)
            val month = docId.substring(2, 4)
            val year = docId.substring(4, 8)
            "$day-$month-$year"
        } else {
            docId // Trả về nguyên gốc nếu không đúng format
        }
    } catch (e: Exception) {
        docId
    }
}

// Hàm chuyển đổi thành ngày đầy đủ với thứ
fun getFullDateString(docId: String): String {
    return try {
        if (docId.length == 8 && docId.all { it.isDigit() }) {
            val day = docId.substring(0, 2).toInt()
            val month = docId.substring(2, 4).toInt()
            val year = docId.substring(4, 8).toInt()

            val calendar = Calendar.getInstance()
            calendar.set(year, month - 1, day)

            val dayOfWeekNames = arrayOf(
                "", "Chủ nhật", "Thứ hai", "Thứ ba", "Thứ tư", "Thứ năm", "Thứ sáu", "Thứ bảy"
            )
            val dayOfWeek = dayOfWeekNames[calendar.get(Calendar.DAY_OF_WEEK)]

            "$dayOfWeek, ngày $day tháng $month năm $year"
        } else {
            "Dữ liệu ngày không hợp lệ"
        }
    } catch (e: Exception) {
        "Lỗi định dạng ngày"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LichSuHoatDongScreen(navController: NavController) {
    var documentIds by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val db = FirebaseFirestore.getInstance()

    // Hàm load dữ liệu
    suspend fun loadDocumentIds() {
        try {
            isLoading = true
            errorMessage = null

            val snapshot = db.collection("lichsuhoatdong").get().await()
            val ids = snapshot.documents
                .map { it.id }
                .sortedByDescending { docId ->
                    try {
                        val sdf = SimpleDateFormat("ddMMyyyy", Locale.getDefault())
                        sdf.parse(docId) ?: Date(0)
                    } catch (e: Exception) {
                        Date(0) // nếu lỗi thì cho về ngày cũ nhất
                    }
                }
            documentIds = ids

        } catch (e: Exception) {
            errorMessage = "Lỗi khi tải dữ liệu: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    // Load dữ liệu khi màn hình được tạo
    LaunchedEffect(Unit) {
        loadDocumentIds()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Top Bar
        TopAppBar(
            title = {
                Text(
                    text = "Lịch Sử Hoạt Động",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Quay lại"
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = {
                        // Refresh dữ liệu
                        kotlinx.coroutines.MainScope().launch {
                            loadDocumentIds()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Làm mới"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF00AEFF),
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White,
                actionIconContentColor = Color.White
            )
        )

        // Nội dung chính
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when {
                isLoading -> {
                    // Hiển thị loading
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFF00AEFF),
                                modifier = Modifier.size(50.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Đang tải dữ liệu...",
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                errorMessage != null -> {
                    // Hiển thị lỗi
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "⚠️ Có lỗi xảy ra",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD32F2F)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = errorMessage!!,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                color = Color(0xFFD32F2F)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    kotlinx.coroutines.MainScope().launch {
                                        loadDocumentIds()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00AEFF))
                            ) {
                                Text("Thử lại")
                            }
                        }
                    }
                }

                documentIds.isEmpty() -> {
                    // Hiển thị khi không có dữ liệu
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "📅",
                                fontSize = 48.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Không có dữ liệu",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Text(
                                text = "Chưa có hoạt động nào trong hệ thống",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                else -> {
                    // Hiển thị danh sách document IDs
                    Column {
                        // Header thông tin
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "📊",
                                    fontSize = 24.sp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Tổng số ngày",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = "${documentIds.size} ngày có hoạt động",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF00AEFF)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Danh sách Document IDs
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(documentIds) { docId ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { navController.navigate("chitiet/$docId") },
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(8.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Icon
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .background(
                                                    Color(0xFF00AEFF).copy(alpha = 0.1f),
                                                    RoundedCornerShape(20.dp)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "📅",
                                                fontSize = 18.sp
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        // Thông tin ngày
                                        Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = formatDocIdToDate(docId),
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF00AEFF)
                                            )
                                            Text(
                                                text = getFullDateString(docId),
                                                fontSize = 13.sp,
                                                color = Color.Gray
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
    }
}