package com.example.tramxeuth.View

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import android.net.Uri

@Composable
fun PaymentWebView(
    url: String,
    navController: NavController
) {
    val context = LocalContext.current
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    // Đặt package nếu muốn mở luôn Google Chrome
    intent.setPackage("com.android.chrome")
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        // Nếu không cài Chrome thì mở browser mặc định
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
//    var currentUrl by remember { mutableStateOf(url) }
//    var canGoBack by remember { mutableStateOf(false) }
//    var canGoForward by remember { mutableStateOf(false) }
//    var isLoading by remember { mutableStateOf(true) }
//    var progress by remember { mutableStateOf(0) }
//
//    Column {
//        // Thanh điều khiển trên cùng
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            IconButton(onClick = { navController.popBackStack() }) {
//                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//            }
//
//            IconButton(onClick = { /* refresh sau */ }) {
//                Icon(Icons.Default.Refresh, contentDescription = "Reload")
//            }
//        }
//
//        // Hiển thị URL
//        Text(
//            text = currentUrl,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(4.dp),
//            maxLines = 1
//        )
//
//        // Progress bar
//        if (isLoading) {
//            LinearProgressIndicator(
//                progress = progress / 100f,
//                modifier = Modifier.fillMaxWidth()
//            )
//        }
//
//        // WebView
//        AndroidView(
//            factory = { context ->
//                WebView(context).apply {
//                    settings.javaScriptEnabled = true
//                    settings.domStorageEnabled = true
//                    settings.javaScriptCanOpenWindowsAutomatically = true
//
//                    webViewClient = WebViewClient()
//                    webChromeClient = object : WebChromeClient() {
//                        override fun onJsAlert(
//                            view: WebView?,
//                            url: String?,
//                            message: String?,
//                            result: JsResult?
//                        ): Boolean {
//                            AlertDialog.Builder(context)
//                                .setMessage(message)
//                                .setPositiveButton("OK") { _, _ -> result?.confirm() }
//                                .create()
//                                .show()
//                            return true
//                        }
//
//                        override fun onJsConfirm(
//                            view: WebView?,
//                            url: String?,
//                            message: String?,
//                            result: JsResult?
//                        ): Boolean {
//                            AlertDialog.Builder(context)
//                                .setMessage(message)
//                                .setPositiveButton("Yes") { _, _ -> result?.confirm() }
//                                .setNegativeButton("No") { _, _ -> result?.cancel() }
//                                .create()
//                                .show()
//                            return true
//                        }
//                    }
//
//                    loadUrl(url)
//                }
//            },
//            update = {
//                canGoBack = it.canGoBack()
//                canGoForward = it.canGoForward()
//            },
//            modifier = Modifier.weight(1f)
//        )
//    }
}