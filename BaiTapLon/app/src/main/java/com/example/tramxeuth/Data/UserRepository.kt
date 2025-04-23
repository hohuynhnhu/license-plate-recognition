package com.example.tramxeuth.Data

import android.content.ContentValues.TAG
import android.util.Log
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


}