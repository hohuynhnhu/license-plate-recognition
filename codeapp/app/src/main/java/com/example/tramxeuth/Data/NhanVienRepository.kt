package com.example.tramxeuth.Data

import com.example.tramxeuth.Model.NhanVien
import com.google.firebase.firestore.FirebaseFirestore

class NhanVienRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun getNhanVienId(uid: String, onResult: (NhanVien?) -> Unit) {
        db.collection("thongtindangky").document(uid).get()
            .addOnSuccessListener { result ->
                val nhanVien = result.toObject(NhanVien::class.java)
                onResult(nhanVien)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }
}