package com.example.tramxeuth.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    var user by mutableStateOf<FirebaseUser?>(auth.currentUser)
    var error by mutableStateOf<String?>(null)
    var loading by mutableStateOf(false)

    fun register(email: String, password: String, onSuccess: () -> Unit, ten: String, mssv: String, biensoxe: String) {
        loading = true
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                loading = false
                if (task.isSuccessful) {
                    user = auth.currentUser
                    saveUserToFirestore(ten, mssv, biensoxe)
                    onSuccess()
                } else {
                    error = task.exception?.message
                }
            }
    }

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        loading = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                loading = false
                if (task.isSuccessful) {
                    user = auth.currentUser
                    onSuccess()
                } else {
                    error = task.exception?.message
                }
            }
    }

    private fun saveUserToFirestore(ten: String, mssv: String, biensoxe: String) {
        val uid = auth.currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        val userMap = mapOf(
            "ten" to ten,
            "email" to auth.currentUser?.email,
            "mssv" to mssv,
            "biensoxe" to biensoxe
        )
        db.collection("thongtindangky").document(uid).set(userMap)
    }

    fun logout(onLoggedOut: () -> Unit) {
        auth.signOut()
        user = null
        onLoggedOut()
    }
}