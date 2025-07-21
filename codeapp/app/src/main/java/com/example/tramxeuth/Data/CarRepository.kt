package com.example.tramxeuth.Data

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

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
}