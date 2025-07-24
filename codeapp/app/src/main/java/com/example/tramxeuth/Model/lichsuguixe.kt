package com.example.tramxeuth.Model

data class ParkingRecord(
    val date: String?,
    val vehicles: List<VehicleInfo>?,
)

data class TimeLine(
    val timeOut: String?,
    val timeIn: String?,
    val imageOut: String?,
    val imageIn: String?,
){
    constructor() : this(null, null, null, null) // 👈 thêm constructor không tham số
}

data class VehicleInfo(
    val totalIn: Int?,
    val totalOut: Int?,
    val licensePlate: String?,
    val timelines: List<TimeLine>? = emptyList()
)
