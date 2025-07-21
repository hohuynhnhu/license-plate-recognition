package com.example.tramxeuth.Data

import android.content.ContentValues.TAG
import android.util.Log
import com.example.tramxeuth.Model.BienSoPhu
import com.example.tramxeuth.Model.biensotrongbai
import com.example.tramxeuth.Model.thongtindangky
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val auth: FirebaseAuth  = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

) {

    suspend fun getCurrentUser(): thongtindangky? {
        val uid = auth.currentUser?.uid ?: return null
        val doc = db.collection("thongtindangky").document(uid).get().await()

        return if (doc.exists()) {
            doc.toObject(thongtindangky::class.java)
        } else {
            null
        }
    }

    suspend fun themBienSoPhu(bienSoMoi: BienSoPhu): Boolean {
        val uid = auth.currentUser?.uid ?: return false
        val docRef = db.collection("thongtindangky").document(uid)
        docRef.update("biensophu", bienSoMoi).await()
        return true
    }

    suspend fun xoaBienSoPhu(bienSoCanXoa: String): Boolean {
        val uid = auth.currentUser?.uid ?: return false
        val docRef = db.collection("thongtindangky").document(uid)

        val snapshot = docRef.get().await()
        val userData = snapshot.toObject(thongtindangky::class.java) ?: return false

        // Chỉ xóa nếu trùng biển
        if (userData.biensophu?.bienSo == bienSoCanXoa) {
            docRef.update("biensophu", null).await()
        }

        return true
    }

    suspend fun giaHanBienSoPhu(bienSo: String): Boolean {
        val uid = auth.currentUser?.uid ?: return false
        val docRef = db.collection("thongtindangky").document(uid)

        val snapshot = docRef.get().await()
        val userData = snapshot.toObject(thongtindangky::class.java) ?: return false

        val currentBienSoPhu = userData.biensophu ?: return false

        if (currentBienSoPhu.bienSo == bienSo) {
            val updated = currentBienSoPhu.copy(createdAt = System.currentTimeMillis())
            docRef.update("biensophu", updated).await()
        }

        return true
    }

    fun BienSoPhu.isExpired(): Boolean {
        val now = System.currentTimeMillis()
        return (now - createdAt) > 24 * 60 * 60 * 1000 // quá 24 giờ
    }

    suspend fun removeExpiredBienSoPhu(): Boolean {
        val uid = auth.currentUser?.uid ?: return false
        val docRef = db.collection("thongtindangky").document(uid)

        val snapshot = docRef.get().await()
        val userData = snapshot.toObject(thongtindangky::class.java) ?: return false
        val current = userData.biensophu ?: return true

        if (current.isExpired()) {
            docRef.update("biensophu", null).await()
        }

        return true
    }

}