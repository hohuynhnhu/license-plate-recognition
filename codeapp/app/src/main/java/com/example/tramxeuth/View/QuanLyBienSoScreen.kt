package com.example.tramxeuth.View

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tramxeuth.Model.*
import com.example.tramxeuth.ViewModel.BienSoViewModel
import androidx.compose.material.icons.filled.ArrowBack

@Composable
fun QuanLyBienSoScreen(
    navController: NavController,
    bienSoViewModel: BienSoViewModel = viewModel()
) {
    val uiState by bienSoViewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    var showFilterSheet by remember { mutableStateOf(false) }

    // Realtime listen khi vào màn hình
    LaunchedEffect(Unit) {
        bienSoViewModel.startRealtimeListen()
    }

    // Lazy load: khi scroll gần cuối thì load thêm
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = listState.layoutInfo.totalItemsCount
            lastVisible >= total - 3 // còn 3 item cuối thì load thêm
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) bienSoViewModel.loadMore()
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp)) {

        // ---- HEADER ----
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Quay lại"
                )
            }
            Text(
                "Quản lý bãi đỗ",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Text(
                "Tổng: ${uiState.pagingState.totalCount}",
                fontSize = 13.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { showFilterSheet = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00AEFF)),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("Lọc", fontSize = 13.sp)
            }
        }

        // ---- SEARCH BAR ----
        OutlinedTextField(
            value = uiState.pagingState.filter.searchQuery,
            onValueChange = { bienSoViewModel.updateSearch(it) },
            placeholder = { Text("Tìm biển số...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            shape = RoundedCornerShape(12.dp)
        )

        // ---- FILTER CHIPS ----
        FilterChipsRow(filter = uiState.pagingState.filter, onFilterChange = {
            bienSoViewModel.updateFilter(it)
        })

        Spacer(modifier = Modifier.height(8.dp))

        // ---- LOADING ----
        if (uiState.pagingState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Column
        }

        // ---- ERROR ----
        uiState.pagingState.error?.let {
            Text("Lỗi: $it", color = Color.Red, modifier = Modifier.padding(8.dp))
        }

        // ---- DANH SÁCH ----
        val displayItems = uiState.filteredItems.ifEmpty { uiState.pagingState.items }

        LazyColumn(state = listState, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(displayItems, key = { it.firebaseKey }) { item ->
                BienSoCard(item = item)
            }

            if (uiState.pagingState.isLoadingMore) {
                item {
                    Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
            }
        }
    }

    // ---- FILTER BOTTOM SHEET ----
    if (showFilterSheet) {
        FilterBottomSheet(
            currentFilter = uiState.pagingState.filter,
            onApply = {
                bienSoViewModel.updateFilter(it)
                showFilterSheet = false
            },
            onDismiss = { showFilterSheet = false }
        )
    }
}

// ==================== CARD XE ====================

@Composable
fun BienSoCard(item: BienSoItem) {
    val data = item.data
    val loaiXeColor = when {
        data.khach == true  -> Color(0xFF1565C0) // Xanh đậm - khách ưu tiên
        data.khach == false -> Color(0xFF0288D1) // Xanh nhạt - khách thường
        else                -> Color(0xFF2E7D32) // Xanh lá - xe đã đăng ký
    }
    val loaiXeLabel = when {
        data.khach == true  -> "Khách ưu tiên"
        data.khach == false -> "Khách thường"
        else                -> "Xe đăng ký"
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cột trái - thông tin chính
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.firebaseKey, // Biển số = key Firebase
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (data.canhbao == true) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = "Cảnh báo",
                            tint = Color(0xFFE53935),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Vào: ${data.timestamp ?: "--"}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                if (data.timeExpired != null) {
                    Text(
                        text = "Hết hạn: ${data.timeExpired}",
                        fontSize = 12.sp,
                        color = Color(0xFFE65100)
                    )
                }
            }

            // Cột phải - badges
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                // Badge loại xe
                Badge(loaiXeLabel, loaiXeColor)
                // Badge trạng thái
                if (data.trangthai == true) {
                    Badge("Trong bãi", Color(0xFF388E3C))
                } else {
                    Badge("Đã ra", Color(0xFF757575))
                }
            }
        }
    }
}

@Composable
fun Badge(text: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color, shape = RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

// ==================== FILTER CHIPS ====================

@Composable
fun FilterChipsRow(filter: BienSoFilter, onFilterChange: (BienSoFilter) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        // Chip trạng thái
        FilterChip(
            selected = filter.trangThai == TrangThaiFilter.TRONG_BAI,
            onClick = {
                onFilterChange(filter.copy(
                    trangThai = if (filter.trangThai == TrangThaiFilter.TRONG_BAI)
                        TrangThaiFilter.TAT_CA else TrangThaiFilter.TRONG_BAI
                ))
            },
            label = { Text("Trong bãi", fontSize = 12.sp) }
        )
        // Chip cảnh báo
        FilterChip(
            selected = filter.canhBaoFilter == CanhBaoFilter.CO_CANH_BAO,
            onClick = {
                onFilterChange(filter.copy(
                    canhBaoFilter = if (filter.canhBaoFilter == CanhBaoFilter.CO_CANH_BAO)
                        CanhBaoFilter.TAT_CA else CanhBaoFilter.CO_CANH_BAO
                ))
            },
            label = { Text("⚠ Cảnh báo", fontSize = 12.sp) }
        )
    }
}

// ==================== FILTER BOTTOM SHEET ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    currentFilter: BienSoFilter,
    onApply: (BienSoFilter) -> Unit,
    onDismiss: () -> Unit
) {
    var filter by remember { mutableStateOf(currentFilter) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Bộ lọc", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            // Lọc loại xe
            Text("Loại xe", fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LoaiXe.entries.forEach { loai ->
                    val label = when (loai) {
                        LoaiXe.TAT_CA            -> "Tất cả"
                        LoaiXe.KHACH_UU_TIEN     -> "Ưu tiên"
                        LoaiXe.KHACH_BINH_THUONG -> "Thường"
                        LoaiXe.XE_DA_DANG_KY     -> "Đăng ký"
                    }
                    FilterChip(
                        selected = filter.loaiXe == loai,
                        onClick = { filter = filter.copy(loaiXe = loai) },
                        label = { Text(label, fontSize = 12.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Lọc giờ
            Text("Lọc theo giờ vào", fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = filter.tuGio?.toString() ?: "",
                    onValueChange = { filter = filter.copy(tuGio = it.toIntOrNull()) },
                    label = { Text("Từ giờ") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = filter.denGio?.toString() ?: "",
                    onValueChange = { filter = filter.copy(denGio = it.toIntOrNull()) },
                    label = { Text("Đến giờ") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { filter = BienSoFilter(); onApply(BienSoFilter()) },
                    modifier = Modifier.weight(1f)
                ) { Text("Xóa lọc") }

                Button(
                    onClick = { onApply(filter) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00AEFF))
                ) { Text("Áp dụng") }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
