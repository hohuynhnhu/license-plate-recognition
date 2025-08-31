package com.example.tramxeuth.Data

import com.example.tramxeuth.Model.LichSuBV
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class LichSuBVRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

) {
    fun getYeuCauByCCCD(cccd: String, onResult: (List<LichSuBV>) -> Unit) {
        db.collection("lichsuyeucau")
            .whereEqualTo("CCCD", cccd)
            .get()
            .addOnSuccessListener { result ->
                val yeuCauList = result.toObjects(LichSuBV::class.java)
                onResult(yeuCauList)
            }
            .addOnFailureListener {
                onResult(emptyList())
    }
}}