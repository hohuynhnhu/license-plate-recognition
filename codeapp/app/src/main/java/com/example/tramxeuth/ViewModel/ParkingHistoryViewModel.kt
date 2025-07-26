package com.example.tramxeuth.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tramxeuth.Data.UserRepository
import com.example.tramxeuth.Model.ParkingRecord
import com.example.tramxeuth.Model.TimeLine
import com.example.tramxeuth.Model.VehicleInfo
import com.example.tramxeuth.Model.thongtindangky
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ParkingHistoryViewModel: ViewModel() {
    val db = FirebaseFirestore.getInstance()
    private val _listParkingHistory = MutableStateFlow<List<ParkingRecord>>(emptyList())
    val listParkingHistory: StateFlow<List<ParkingRecord>?> = _listParkingHistory

    fun getParkingHistoryById(biensoxe: String) {
        viewModelScope.launch {
            try {
                val result = db.collection("lichsuhoatdong").get().await()
                val matchedHistory = mutableListOf<ParkingRecord>()
                Log.d("Firestore", "Số lượng tài liệu: ${result.documents.size}")
                for (document in result.documents) {
                    Log.d("Firestore", "Document ID: ${document.id}")
                    val date = document.id
                    val xeSnapshot = document.reference.collection("xe").get().await()

                    val matchedVehicles = xeSnapshot.documents.mapNotNull { xeDoc ->
                        if (xeDoc.id == biensoxe) {
                            val timelineSnapshot =
                                xeDoc.reference.collection("timeline").get().await()
                            val listTimeLine =
                                timelineSnapshot.documents.mapNotNull { timelineDoc ->
                                    timelineDoc.toObject(TimeLine::class.java)
                                }

                            val solanvao = xeDoc.getLong("solanvao")?.toInt() ?: 0
                            val solanra = xeDoc.getLong("solanra")?.toInt() ?: 0

                            VehicleInfo(
                                totalIn = solanvao,
                                totalOut = solanra,
                                licensePlate = xeDoc.id,
                                timelines = listTimeLine
                            )
                        } else null
                    }

                    if (matchedVehicles.isNotEmpty()) {
                        matchedHistory.add(ParkingRecord(date = date, vehicles = matchedVehicles))
                    }
                }

                _listParkingHistory.value = matchedHistory

            } catch (e: Exception) {
                Log.e("Firestore", "Lỗi: ", e)
            }
        }
    }
}

fun convertDate(date: String): String{
    return "${date.substring(0, 2)}/${date.substring(2, 4)}/${date.substring(4)}"
}