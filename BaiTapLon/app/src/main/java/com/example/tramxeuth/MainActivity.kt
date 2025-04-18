    package com.example.tramxeuth

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.tramxeuth.View.SignForm
import com.example.tramxeuth.ui.theme.TramXeUTHTheme
import com.google.firebase.firestore.FirebaseFirestore

    class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val db = FirebaseFirestore.getInstance()
        val user = hashMapOf(
            "mssv" to "2251111111",
            "name" to "Tiktoker Lê Tuấn Khang",
            "cccd" to "1111111111"
        )

        db.collection("thongtinsinhvien")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error adding document", e)
            }
        db.collection("thongtinsinhvien")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("Firestore", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents.", exception)
            }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TramXeUTHTheme {
                SignForm("Đăng nhập")
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TramXeUTHTheme {
        Greeting("Android")
    }
}