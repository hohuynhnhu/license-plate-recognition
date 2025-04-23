package com.example.tramxeuth.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tramxeuth.Data.UserRepository
import com.example.tramxeuth.Model.biensotrongbai
import com.example.tramxeuth.Model.thongtindangky
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val userRepository = UserRepository()
    var currentUser by mutableStateOf<thongtindangky?>(null)
        private set

    fun loadUserData() {
        viewModelScope.launch {
            currentUser = userRepository.getCurrentUser()
        }
    }
    fun clearUserData() {
        currentUser = null
    }
}