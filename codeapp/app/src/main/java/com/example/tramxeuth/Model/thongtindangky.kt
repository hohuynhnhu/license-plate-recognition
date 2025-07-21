package com.example.tramxeuth.Model

data class thongtindangky(
    var ten: String ="",
    var email: String="",
    var mssv: String="",
    var biensoxe: String="",
    var biensophu: BienSoPhu? = null
)
data class BienSoPhu(
    var bienSo: String = "",
    var ngayHetHan: String? = null,
    var trangThai: String = "active",
    var createdAt: Long = System.currentTimeMillis()
)