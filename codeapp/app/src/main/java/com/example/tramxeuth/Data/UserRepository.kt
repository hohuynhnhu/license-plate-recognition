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
import java.util.Calendar
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
        return try {
            val uid = auth.currentUser?.uid ?: return false
            val docRef = db.collection("thongtindangky").document(uid)

            val snapshot = docRef.get().await()
            val userData = snapshot.toObject(thongtindangky::class.java) ?: return false

            val bienSoPhu = userData.biensophu ?: return false
            if (bienSoPhu.bienSo != bienSoCanXoa) return false

            val trangThaiRef = FirebaseDatabase.getInstance()
                .getReference("biensotrongbai")
                .child(bienSoCanXoa)
                .child("trangthai")

            val trangThaiSnapshot = trangThaiRef.get().await()
            val trangThai = trangThaiSnapshot.getValue(String::class.java)

            if (trangThai == null) {
                docRef.update("biensophu", null).await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("Repo", "Lỗi khi xóa biển số phụ: ${e.message}", e)
            false
        }
    }

    suspend fun getCarTimestamp(bienSo: String): Long? {
        val snapshot = FirebaseDatabase.getInstance()
            .getReference("biensotrongbai")
            .child(bienSo)
            .child("timestamp")
            .get()
            .await()

        val timestampStr = snapshot.getValue(String::class.java)
        return try {
            // Parse từ chuỗi về millis
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            format.parse(timestampStr ?: "")?.time
        } catch (e: Exception) {
            null
        }
    }

    suspend fun giaHanBienSoPhu(bienSo: String): Boolean {
        val uid = auth.currentUser?.uid ?: return false
        val docRef = db.collection("thongtindangky").document(uid)
        val quyDinhRef = db.collection("quydinh").limit(1).get().await()

        val quyDinhDoc = quyDinhRef.documents.firstOrNull()
        val giaHanDuocPhep = quyDinhDoc?.getBoolean("giahan") ?: false
        if (!giaHanDuocPhep) return false

        val gioiHanGio = quyDinhDoc?.getLong("gioihangio")
        val gioiHanNgay = quyDinhDoc?.getTimestamp("gioihanngay")?.toDate()

        val snapshot = docRef.get().await()
        val userData = snapshot.toObject(thongtindangky::class.java) ?: return false
        val currentBienSoPhu = userData.biensophu ?: return false
        if (currentBienSoPhu.bienSo != bienSo) return false

        //  Lấy createdAt từ Realtime Database (nếu có)
        val createdAt = getCarTimestamp(bienSo) ?: System.currentTimeMillis()

        val newExpiry: Long = when {
            gioiHanGio != null -> createdAt + gioiHanGio * 60 * 60 * 1000
            gioiHanNgay != null -> {
                Calendar.getInstance().apply {
                    time = gioiHanNgay
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                    set(Calendar.SECOND, 59)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
            }
            else -> return false
        }

        val updated = currentBienSoPhu.copy(
            createdAt = createdAt,
            ngayHetHan = newExpiry
        )
        docRef.update("biensophu", updated).await()

        // Cập nhật lên Realtime Database: timeExpired
        val realtimeRef = FirebaseDatabase.getInstance()
            .getReference("biensotrongbai")
            .child(bienSo)
            .child("timeExpired")

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formattedExpiry = sdf.format(Date(newExpiry))

        realtimeRef.setValue(formattedExpiry).await()
        return true
    }

    suspend fun removeExpiredBienSoPhu(): Boolean {
        val uid = auth.currentUser?.uid ?: return false
        val docRef = db.collection("thongtindangky").document(uid)
        val snapshot = docRef.get().await()
        val userData = snapshot.toObject(thongtindangky::class.java) ?: return false
        val bienSoPhu = userData.biensophu ?: return true

        // Lấy dữ liệu từ collection quydinh
        val quyDinhRef = db.collection("quydinh").limit(1).get().await()
        val quyDinhDoc = quyDinhRef.documents.firstOrNull() ?: return true

        val gioiHanGio = quyDinhDoc.getLong("gioihangio")
        val gioiHanNgay = quyDinhDoc.getTimestamp("gioihanngay")?.toDate()

        val now = System.currentTimeMillis()
        val hetHan = when {
            gioiHanGio != null -> bienSoPhu.ngayHetHan != null && now > bienSoPhu.ngayHetHan!!
            gioiHanNgay != null -> bienSoPhu.ngayHetHan != null && now > bienSoPhu.ngayHetHan!!
            else -> false // Không có giới hạn nào hợp lệ → không xóa
        }

        if (hetHan) {
            docRef.update("biensophu", null).await()
            return true
        }

        return false
    }
}
