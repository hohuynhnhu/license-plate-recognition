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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    suspend fun giaHanBienSoPhu(bienSo: String): GiaHanResult {
        val uid = auth.currentUser?.uid ?: return GiaHanResult.ERROR
        val docRef = db.collection("thongtindangky").document(uid)

        val snapshot = docRef.get().await()
        val userData = snapshot.toObject(thongtindangky::class.java) ?: return GiaHanResult.ERROR

        val currentBienSoPhu = userData.biensophu ?: return GiaHanResult.ERROR

        if (currentBienSoPhu.bienSo == bienSo) {
            val now = System.currentTimeMillis()

            val thoiGianConLai = (currentBienSoPhu.ngayHetHan ?: 0L) - now
            val MILI_GIO = 60 * 60 * 1000

            if (thoiGianConLai > 12 * MILI_GIO) {
                return GiaHanResult.ALREADY_EXTENDED
            }

            val updated = currentBienSoPhu.copy(
                createdAt = now,
                ngayHetHan = now.plus(24 * 60 * 60 * 1000)
            )
            docRef.update("biensophu", updated).await()
            return GiaHanResult.SUCCESS
        }

        return GiaHanResult.ERROR
    }

    suspend fun removeExpiredBienSoPhu(): Boolean {
        val uid = auth.currentUser?.uid ?: return false
        val docRef = db.collection("thongtindangky").document(uid)

        val snapshot = docRef.get().await()
        val userData = snapshot.toObject(thongtindangky::class.java) ?: return false
        val bienSoPhu = userData.biensophu ?: return true

        if (bienSoPhu.isExpired()) {
            docRef.update("biensophu", null).await()
            return true
        }

        return false
    }
    fun BienSoPhu.isExpired(): Boolean {
        val now = System.currentTimeMillis()
        return this.ngayHetHan != null && now > this.ngayHetHan!!
    }
}
enum class GiaHanResult {
    SUCCESS,
    ALREADY_EXTENDED,
    ERROR
}
