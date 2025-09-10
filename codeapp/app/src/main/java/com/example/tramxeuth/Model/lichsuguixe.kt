package com.example.tramxeuth.Model

data class ParkingRecordMotorbike(
    val date: String?,
    val vehicles: List<MotorbikeInfo>?,
)
data class ParkingRecordCar(
    val date: String?,
    val vehicles: List<CarInfo>?,
)

data class TimeLineXeMay(
    val biensoxevao: String?,
    val biensoxera: String?,
    val timeout: String?,
    val timein: String?,
    val khuonmatra: String?,
    val khuonmatvao: String?,
){
    constructor() : this(null, null, null, null, null, null)
}
data class MotorbikeInfo(
    val totalIn: Int?,
    val totalOut: Int?,
    val licensePlate: String?,
    val timelines: List<TimeLineXeMay> = emptyList()
)
data class TimeLineXeOto(
    val biensoxera: String?,
    val biensoxevao: String?,
    val timeout: String?,
    val timein: String?,
    val hinhxera: String?,
    val hinhxevao: String?,
    val logora: List<String>? = emptyList(),
    val logovao: List<String>? = emptyList()
)
{
    constructor() : this(null, null, null, null, null, null, null, null)
}
data class CarInfo(
    val totalIn: Int?,
    val totalOut: Int?,
    val licensePlate: String?,
    val timelines: List<TimeLineXeOto> = emptyList()
)