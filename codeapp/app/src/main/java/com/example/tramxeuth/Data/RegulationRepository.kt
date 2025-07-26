package com.example.tramxeuth.Data

import com.example.tramxeuth.Model.NhanVien
import com.example.tramxeuth.Model.QuyDinh
import com.example.tramxeuth.Model.thongtindangky
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RegulaionRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun getRegulation(): QuyDinh? {
        val snapshot = db.collection("quydinh").limit(1).get().await()

        return if (!snapshot.isEmpty) {
            snapshot.documents[0].toObject(QuyDinh::class.java)
        } else {
            null
        }
    }
}