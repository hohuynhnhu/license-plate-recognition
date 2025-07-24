package com.example.tramxeuth.Model

data class thongtindangky(
    var ten: String ="",
    var email: String="",
    var cccd: String="",
    var biensoxe: String="",
    var biensophu: BienSoPhu? = null
)
data class BienSoPhu(
    var bienSo: String = "",
    var ngayHetHan: Long? = null,
    var createdAt: Long = System.currentTimeMillis()
)