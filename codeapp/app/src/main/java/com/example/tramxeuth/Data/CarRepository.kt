package com.example.tramxeuth.Data

import android.content.ContentValues.TAG
import android.util.Log
import com.example.tramxeuth.Model.thongtindangky
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.firestore.FirebaseFirestore

class CarRepository(
    private val database: FirebaseDatabase = Firebase.database
) {
    private val messagesRef = database.getReference("biensotrongbai")
        // Hàm lắng nghe thay đổi từ Firebase
    fun listenForTrangthaiChanges(bienSo:String, onChanged: (Boolean?) -> Unit) {
        messagesRef.child(bienSo).child("trangthai").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isActive = snapshot.getValue(Boolean::class.java)
                onChanged(isActive)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value of trangthai.", error.toException())
            }
        })
    }

    fun updateTrangThai(bienSo: String, isActive: Boolean, onComplete: (Boolean) -> Unit) {
        messagesRef.child(bienSo).child("trangthai").setValue(isActive)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun listenForCanhbaoChanges(bienSo:String, onChanged: (Boolean?) -> Unit) {
        messagesRef.child(bienSo).child("canhbao").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isActive = snapshot.getValue(Boolean::class.java)
                onChanged(isActive)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value of canh bao.", error.toException())
            }
        })
    }

    fun updateCanhbao(bienSo: String, isActive: Boolean, onComplete: (Boolean) -> Unit) {
        messagesRef.child(bienSo).child("canhbao").setValue(isActive)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun updateNgayHetHanBienSoPhu(biensophu: String, ngayHetHan: Long, onComplete: (Boolean) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return onComplete(false)
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("thongtindangky").document(uid)

        docRef.get().addOnSuccessListener { snapshot ->
            val data = snapshot.toObject(thongtindangky::class.java)
            val currentPhu = data?.biensophu

            if (currentPhu?.bienSo == biensophu) {
                val updated = currentPhu.copy(
                    ngayHetHan = ngayHetHan,
                    createdAt = System.currentTimeMillis(),
                )
                docRef.update("biensophu", updated).addOnSuccessListener {
                    onComplete(true)
                }.addOnFailureListener {
                    onComplete(false)
                }
            } else {
                onComplete(false)
            }
        }.addOnFailureListener {
            onComplete(false)
        }
    }

}