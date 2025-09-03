package com.example.tramxeuth.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class TimelineItem(
    val id: String,
    val biensoxera: String? = null,
    val biensoxevao: String? = null,
    val khuonmatra: String? = null,
    val khuonmatvao: String? = null,
    val timein: String? = null,
    val timeout: String? = null
)

class ChiTietTimelineViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    var item by mutableStateOf<TimelineItem?>(null)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun loadTimelineDetail(ngayId: String, collection: String, xeId: String, timelineId: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val snapshot = db.collection("lichsuhoatdong")
                    .document(ngayId)
                    .collection(collection)
                    .document(xeId)
                    .collection("timeline")
                    .document(timelineId)
                    .get()
                    .await()

                item = TimelineItem(
                    id = snapshot.id,
                    biensoxera = snapshot.getString("biensoxera"),
                    biensoxevao = snapshot.getString("biensoxevao"),
                    khuonmatra = snapshot.getString("khuonmatra"),
                    khuonmatvao = snapshot.getString("khuonmatvao"),
                    timein = snapshot.getString("timein"),
                    timeout = snapshot.getString("timeout")
                )
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }
}
