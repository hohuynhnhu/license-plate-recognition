package com.example.tramxeuth.Model

data class ParkingRecord(
    val date: String,
    val licensePlate: String,
    val totalIn: Int,
    val totalOut: Int,
    val type: typeXe,
)
enum class typeXe{
    XeMay,
    XeOto
}