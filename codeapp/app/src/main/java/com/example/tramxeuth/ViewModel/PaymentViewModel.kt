package com.example.tramxeuth.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tramxeuth.Model.Request.PaymentRequest
import com.example.tramxeuth.Retrofit.RetrofitInstance
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PaymentViewModel: ViewModel() {
    private val _urlPayment = MutableStateFlow<String?>(null)
    val urlPayment: StateFlow<String?> = _urlPayment

    fun createPaymentUrl(amount: Int, soluot: Int) {
        viewModelScope.launch {
            try {
                val userId = FirebaseAuth.getInstance().currentUser!!.uid
                val request = PaymentRequest(amount, soluot, userId)
                val response = RetrofitInstance.paymentApi.createPaymentUrl(request)

                if (response.isSuccessful) {
                    _urlPayment.value = response.body()?.paymentUrl
                    Log.d("PaymentVM", _urlPayment.value.toString())
                    // 👉 ở đây bạn có thể emit url ra LiveData/StateFlow để UI nhận
                } else {
                    Log.e("PaymentVM", "Lỗi response: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("PaymentVM", "Lỗi gọi API: ", e)
            }
        }
    }
}