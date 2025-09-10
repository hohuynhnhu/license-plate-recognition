package com.example.tramxeuth.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tramxeuth.Model.CarInfo
import com.example.tramxeuth.Model.MotorbikeInfo
import com.example.tramxeuth.Model.ParkingRecord
import com.example.tramxeuth.Model.ParkingRecordCar
import com.example.tramxeuth.Model.ParkingRecordMotorbike
import com.example.tramxeuth.Model.TimeLineXeMay
import com.example.tramxeuth.Model.TimeLineXeOto
import com.example.tramxeuth.Model.typeXe
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ParkingHistoryViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    // Xe máy
    private val _listParkingHistoryMotorbike = MutableStateFlow<List<ParkingRecordMotorbike>>(emptyList())
    val listParkingHistoryMotorbike: StateFlow<List<ParkingRecordMotorbike>> = _listParkingHistoryMotorbike

    // Ô tô
    private val _listParkingHistoryCar = MutableStateFlow<List<ParkingRecordCar>>(emptyList())
    val listParkingHistoryCar: StateFlow<List<ParkingRecordCar>> = _listParkingHistoryCar

    // Gom chung (cho UI list)
    private val _listParkingHistoryUnified = MutableStateFlow<List<ParkingRecord>>(emptyList())
    val listParkingHistoryUnified: StateFlow<List<ParkingRecord>> = _listParkingHistoryUnified

    suspend fun getParkingHistoryById(biensoxe: String) {
        try {
            val result = db.collection("lichsuhoatdong").get().await()
            val matchedHistoryMotorbike = mutableListOf<ParkingRecordMotorbike>()
            val matchedHistoryCar = mutableListOf<ParkingRecordCar>()

            Log.d("Firestore", "Số lượng tài liệu: ${result.documents.size}")

            for (document in result.documents) {
                Log.d("Firestore", "Document ID: ${document.id}")
                val date = document.id
                val xemaySnapshot = document.reference.collection("xemay").get().await()
                val xeotoSnapshot = document.reference.collection("xeoto").get().await()

                // ✅ Xử lý xe máy
                val matchedVehicles = xemaySnapshot.documents.mapNotNull { xeDoc ->
                    if (xeDoc.id == biensoxe) {
                        val timelineSnapshot = xeDoc.reference.collection("timeline").get().await()
                        val listTimeLineXeMay =
                            timelineSnapshot.documents.mapNotNull { timelineDoc ->
                                Log.d("Firestore", "Timeline: ${timelineDoc.toObject(TimeLineXeMay::class.java)}")
                                timelineDoc.toObject(TimeLineXeMay::class.java)
                            }

                        MotorbikeInfo(
                            totalIn = xeDoc.getLong("solanvao")?.toInt() ?: 0,
                            totalOut = xeDoc.getLong("solanra")?.toInt() ?: 0,
                            licensePlate = xeDoc.id,
                            timelines = listTimeLineXeMay
                        )
                    } else null
                }

                // ✅ Xử lý ô tô
                val matchedVehicles2 = xeotoSnapshot.documents.mapNotNull { xeDoc ->
                    if (xeDoc.id == biensoxe) {
                        val timelineSnapshot = xeDoc.reference.collection("timeline").get().await()
                        Log.d("Firestore", "Số lượng tài liệu timeline oto: ${timelineSnapshot.documents.size}")
                        Log.d("Firestore", "Số tài liệu timeline oto: ${timelineSnapshot.documents}")
                        val listTimeLineXeOto =
                            timelineSnapshot.documents.mapNotNull { timelineDoc ->
                                timelineDoc.toObject(TimeLineXeOto::class.java)
                            }

                        CarInfo(
                            totalIn = xeDoc.getLong("solanvao")?.toInt() ?: 0,
                            totalOut = xeDoc.getLong("solanra")?.toInt() ?: 0,
                            licensePlate = xeDoc.id,
                            timelines = listTimeLineXeOto
                        )
                    } else null
                }

                if (matchedVehicles.isNotEmpty()) {
                    matchedHistoryMotorbike.add(
                        ParkingRecordMotorbike(date = date, vehicles = matchedVehicles)
                    )
                }
                if (matchedVehicles2.isNotEmpty()) {
                    matchedHistoryCar.add(
                        ParkingRecordCar(date = date, vehicles = matchedVehicles2)
                    )
                }
            }

            // cập nhật state
            _listParkingHistoryMotorbike.value = matchedHistoryMotorbike
            _listParkingHistoryCar.value = matchedHistoryCar

        } catch (e: Exception) {
            Log.e("Firestore", "Lỗi: ", e)
        }
    }

    fun getUnifiedParkingHistory(biensoxe: String) {
        viewModelScope.launch {
            getParkingHistoryById(biensoxe)

            val unified = mutableListOf<ParkingRecord>()

            _listParkingHistoryMotorbike.value.forEach { record ->
                record.vehicles?.forEach { vehicle ->
                    if(vehicle.licensePlate == biensoxe){
                    unified.add(
                        ParkingRecord(
                            date = record.date ?: "",
                            licensePlate = vehicle.licensePlate ?: "",
                            totalIn = vehicle.totalIn ?: 0,
                            totalOut = vehicle.totalOut ?: 0,
                            type = typeXe.XeMay
                        )
                    )
                }}
            }

            _listParkingHistoryCar.value.forEach { record ->

                record.vehicles?.forEach { vehicle ->
                    if(vehicle.licensePlate == biensoxe){
                        unified.add(
                            ParkingRecord(
                                date = record.date ?: "",
                                licensePlate = vehicle.licensePlate ?: "",
                                totalIn = vehicle.totalIn ?: 0,
                                totalOut = vehicle.totalOut ?: 0,
                                type = typeXe.XeOto
                            )
                        )
                    }
                }

            }

            _listParkingHistoryUnified.value = unified
        }
    }
}

// Hàm tiện ích
fun convertDate(date: String): String {
    return "${date.substring(0, 2)}/${date.substring(2, 4)}/${date.substring(4)}"
}
