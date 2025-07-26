package com.example.tramxeuth.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    var user by mutableStateOf<FirebaseUser?>(auth.currentUser)
    var error by mutableStateOf<String?>(null)
    var loading by mutableStateOf(false)

    fun register(email: String, password: String, onSuccess: () -> Unit, ten: String, cccd: String, biensoxe: String) {
        loading = true
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                loading = false
                if (task.isSuccessful) {
                    user = auth.currentUser
                    saveUserToFirestore(ten, cccd, biensoxe)
                    onSuccess()
                } else {
                    error = task.exception?.message
                }
            }
    }

    fun login(email: String, password: String, onSuccess: (String) -> Unit) {
        loading = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                loading = false
                if (task.isSuccessful) {
                    user = auth.currentUser
                    saveFcmTokenToFirestore()
//                    onSuccess()
                    getUserRole{
                        role->
                        if(role != null){
                            onSuccess(role)
                        }else{
                            error = "Không tìm thấy role"
                        }
                    }
                } else {
                    error = task.exception?.message
                }
            }
    }

    private fun saveFcmTokenToFirestore() {
        val uid = auth.currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                val userMap = mapOf(
                    "fcmTokens" to FieldValue.arrayUnion(token)
                )
                db.collection("thongtindangky").document(uid).set(userMap, SetOptions.merge())
            }
            .addOnFailureListener { e ->
                Log.e("FCM", "Failed to get FCM token", e)
            }
    }

    private fun saveUserToFirestore(ten: String, cccd: String, biensoxe: String) {
        val uid = auth.currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        val userMap = mapOf(
            "ten" to ten,
            "email" to auth.currentUser?.email,
            "cccd" to cccd,
            "biensoxe" to biensoxe,
            "role" to "user"
        )
        db.collection("thongtindangky").document(uid).set(userMap)
    }

    fun logout(onLoggedOut: () -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            auth.signOut()
            user = null
            onLoggedOut()
            return
        }

        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                val db = FirebaseFirestore.getInstance()
                val updates = mapOf(
                    "fcmTokens" to FieldValue.arrayRemove(token)
                )

                db.collection("thongtindangky").document(uid)
                    .update(updates)
                    .addOnCompleteListener {
                        auth.signOut()
                        user = null
                        onLoggedOut()
                    }
            }
            .addOnFailureListener {
                auth.signOut()
                user = null
                onLoggedOut()
            }
    }
    fun getUserRole(onResult: (String?) -> Unit) {
        val uid =auth.currentUser?.uid?:return onResult(null)
        val db = FirebaseFirestore.getInstance()
        db.collection("thongtindangky").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val role = document.getString("role")
                    onResult(role)
                } else {
                    onResult(null)
                    error = "Không tìm thấy role"
                }
            }
            .addOnFailureListener { exception ->
                onResult(null)
    }
}
}