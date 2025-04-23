package com.example.tramxeuth.View

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tramxeuth.ViewModel.MessViewModel

@Composable
fun MainScreen(messViewModel: MessViewModel = viewModel()) {
    val token by messViewModel.fcmToken.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text("Your FCM Token:", style = MaterialTheme.typography.titleMedium)
        Text(token, style = MaterialTheme.typography.bodySmall)
    }
}