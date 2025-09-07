package com.example.tramxeuth.Data

import com.example.tramxeuth.Model.BaoRung
import com.google.firebase.database.*

class BaoRungRepository(
    private val db: FirebaseDatabase = FirebaseDatabase.getInstance()
) {
    private val canhBaoRef: DatabaseReference = db.getReference("CanhBaoRung")

    // Lắng nghe thay đổi từ Firebase Realtime Database
    fun listenCanhBaoRung(onValueChange: (BaoRung) -> Unit) {
        canhBaoRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue(Boolean::class.java) ?: false
                onValueChange(BaoRung(value))
            }
            override fun onCancelled(error: DatabaseError) {
                //log lỗi
            }
        })
    }

    // Hàm cập nhật giá trị cảnh báo
    fun setCanhBaoRung(value: Boolean) {
        canhBaoRef.setValue(value)
    }
}
