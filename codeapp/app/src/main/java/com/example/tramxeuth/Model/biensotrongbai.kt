package com.example.tramxeuth.Model

// ==================== REALTIME DB MODEL ====================

data class BienSoTrongBai(
    var khach: Boolean? = null,
    var biensoxe: String? = null,
    var timestamp: String? = null,
    var timeExpired: String? = null,
    var trangthai: Boolean? = null,
    var canhbao: Boolean? = null
)

data class BienSoItem(
    val firebaseKey: String = "",
    val data: BienSoTrongBai = BienSoTrongBai()
)

// ==================== ENUM ====================

enum class LoaiXe {
    TAT_CA,
    KHACH_UU_TIEN,      // khach = true
    KHACH_BINH_THUONG,  // khach = false
    XE_DA_DANG_KY       // khach = null (không có trường trong DB)
}

enum class TrangThaiFilter {
    TAT_CA,
    TRONG_BAI,  // trangthai = true
    DANG_RA     // trangthai = false
}

enum class CanhBaoFilter {
    TAT_CA,
    CO_CANH_BAO,     // canhbao = true
    KHONG_CANH_BAO   // canhbao = false
}

// ==================== EXTENSION FILTER ====================

fun BienSoItem.matchLoaiXe(filter: LoaiXe): Boolean {
    return when (filter) {
        LoaiXe.TAT_CA             -> true
        LoaiXe.KHACH_UU_TIEN      -> data.khach == true
        LoaiXe.KHACH_BINH_THUONG  -> data.khach == false
        LoaiXe.XE_DA_DANG_KY      -> data.khach == null
    }
}

fun BienSoItem.matchTrangThai(filter: TrangThaiFilter): Boolean {
    return when (filter) {
        TrangThaiFilter.TAT_CA    -> true
        TrangThaiFilter.TRONG_BAI -> data.trangthai == true
        TrangThaiFilter.DANG_RA   -> data.trangthai == false
    }
}

fun BienSoItem.matchCanhBao(filter: CanhBaoFilter): Boolean {
    return when (filter) {
        CanhBaoFilter.TAT_CA          -> true
        CanhBaoFilter.CO_CANH_BAO     -> data.canhbao == true
        CanhBaoFilter.KHONG_CANH_BAO  -> data.canhbao == false
    }
}

fun BienSoItem.matchSearch(query: String): Boolean {
    if (query.isBlank()) return true
    return data.biensoxe?.contains(query.trim(), ignoreCase = true) == true
}

fun BienSoItem.matchThoiGian(tuGio: Int?, denGio: Int?): Boolean {
    if (tuGio == null && denGio == null) return true
    val timestamp = data.timestamp ?: return true
    // Parse giờ từ string "yyyy-MM-dd HH:mm:ss"
    return try {
        val hour = timestamp.substring(11, 13).toInt()
        val from = tuGio ?: 0
        val to = denGio ?: 23
        hour in from..to
    } catch (e: Exception) { true }
}

// Gộp tất cả filter lại - dùng trong ViewModel
fun BienSoItem.matchFilter(filter: BienSoFilter): Boolean {
    return matchLoaiXe(filter.loaiXe)
            && matchTrangThai(filter.trangThai)
            && matchCanhBao(filter.canhBaoFilter)
            && matchSearch(filter.searchQuery)
            && matchThoiGian(filter.tuGio, filter.denGio)
}

// ==================== FILTER STATE ====================

data class BienSoFilter(
    val searchQuery: String = "",
    val tuGio: Int? = null,
    val denGio: Int? = null,
    val loaiXe: LoaiXe = LoaiXe.TAT_CA,
    val trangThai: TrangThaiFilter = TrangThaiFilter.TAT_CA,
    val canhBaoFilter: CanhBaoFilter = CanhBaoFilter.TAT_CA
)

// ==================== PAGING & UI STATE ====================

data class BienSoPagingState(
    val items: List<BienSoItem> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val lastKey: String? = null,
    val hasMore: Boolean = true,
    val error: String? = null,
    val filter: BienSoFilter = BienSoFilter(),
    val totalCount: Int = 0
)

data class BienSoUiState(
    val pagingState: BienSoPagingState = BienSoPagingState(),
    val filteredItems: List<BienSoItem> = emptyList(),
    val selectedItem: BienSoItem? = null
)