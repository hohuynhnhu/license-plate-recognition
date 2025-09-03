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
fun ChiTietHoatDongScreen(navController: NavController, docId: String) {
    val db = FirebaseFirestore.getInstance()
    var xeList by remember { mutableStateOf<List<String>>(emptyList()) }
    var xeMayList by remember { mutableStateOf<List<String>>(emptyList()) }
    var xeOtoList by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Load subcollection
    suspend fun loadSubCollections() {
        try {
            isLoading = true
            errorMessage = null

            val xeSnapshot = db.collection("lichsuhoatdong")
                .document(docId)
                .collection("xe")
                .get()
                .await()

            val xemaySnapshot = db.collection("lichsuhoatdong")
                .document(docId)
                .collection("xemay")
                .get()
                .await()

            val xeotoSnapshot = db.collection("lichsuhoatdong")
                .document(docId)
                .collection("xeoto")
                .get()
                .await()

            xeList = xeSnapshot.documents.map { it.id }
            xeMayList = xemaySnapshot.documents.map { it.id }
            xeOtoList = xeotoSnapshot.documents.map { it.id }

        } catch (e: Exception) {
            errorMessage = "Lỗi khi tải dữ liệu: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadSubCollections()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = getFullDateString(docId),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
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
                    Text(
                        text = errorMessage ?: "Có lỗi xảy ra",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                xeList.isEmpty() && xeMayList.isEmpty() && xeOtoList.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("📭", fontSize = 40.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Không có dữ liệu", color = Color.Gray)
                        }
                    }
                }

                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (xeList.isNotEmpty()) {
                            item {
                                Text(
                                    " Xe",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = Color(0xFF00AEFF)
                                )
                            }
                            items(xeList) { xeId ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                        navController.navigate("chitietxe/$docId/xe/$xeId")
                                    },
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(8.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Text(
                                        text = xeId,
                                        modifier = Modifier.padding(16.dp),
                                        fontSize = 18.sp
                                    )
                                }
                            }
                        }

                        if (xeMayList.isNotEmpty()) {
                            item {
                                Text(
                                    "🏍️ Xe máy",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = Color(0xFF00AEFF)
                                )
                            }
                            // Cho xe máy
                            items(xeMayList) { xeMayId ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            navController.navigate("chitietxe/$docId/xemay/$xeMayId")
                                        },
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(8.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Text(
                                        text = xeMayId,
                                        modifier = Modifier.padding(16.dp),
                                        fontSize = 18.sp
                                    )
                                }
                            }
                        }

                        if (xeOtoList.isNotEmpty()) {
                            item {
                                Text(
                                    "🚙 Xe ô tô",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = Color(0xFF00AEFF)
                                )
                            }
                            items(xeOtoList) { xeOtoId ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            navController.navigate("chitietxe/$docId/xeoto/$xeOtoId")
                                        },
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(8.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Text(
                                        text = xeOtoId,
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
