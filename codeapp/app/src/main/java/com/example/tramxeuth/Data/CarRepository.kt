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
class
CarRepository(
    private val database: FirebaseDatabase = Firebase.database
) {
    // Auto leave functions
    fun updateAutoLeave(bienSo: String, autoLeave: Boolean, onComplete: (Boolean) -> Unit) {
        messagesRef.child(bienSo).child("autoLeave").setValue(autoLeave)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun listenForAutoLeaveChanges(bienSo: String, onChanged: (Boolean?) -> Unit) {
        messagesRef.child(bienSo).child("autoLeave").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val autoLeave = snapshot.getValue(Boolean::class.java)
                onChanged(autoLeave)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("CarRepository", "Failed to read autoLeave value.", error.toException())
            }
        })
    }
    private val messagesRef = database.getReference("biensotrongbai")
        // Hàm lắng nghe thay đổi từ Realtime Database
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

    fun getTimeExpired(bienSo: String, onResult: (String?) -> Unit) {
        val timestampRef = messagesRef.child(bienSo).child("timeExpired")

        timestampRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val timeExpired = snapshot.getValue(String::class.java)
                onResult(timeExpired)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read timestamp.", error.toException())
                onResult(null)
            }
        })
    }
}