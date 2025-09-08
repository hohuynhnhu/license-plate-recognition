package com.example.tramxeuth.View

import android.media.MediaPlayer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tramxeuth.ViewModel.BaoRungViewModel
import com.example.tramxeuth.R

@Composable
fun BaoRungDialog(
    baoRungViewModel: BaoRungViewModel = viewModel()
) {
    val canhBao = baoRungViewModel.canhBaoRung.collectAsState()
    val context = LocalContext.current

    // Quản lý MediaPlayer
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    // Khi canhBao thay đổi -> phát hoặc tắt âm thanh
    LaunchedEffect(canhBao.value.CanhBaoRung) {
        if (canhBao.value.CanhBaoRung) {
            // Bật cảnh báo -> phát âm thanh lặp
            mediaPlayer = MediaPlayer.create(context, R.raw.alert_sound)
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
        } else {
            // Tắt cảnh báo -> dừng âm thanh
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    // Animation vô hạn
    val infiniteTransition = rememberInfiniteTransition(label = "borderAnim")

    val blinkColor by infiniteTransition.animateColor(
        initialValue = Color(0xFFFFA726), // cam nhạt
        targetValue = Color(0xFFFF7043), // cam đậm
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "blink"
    )

    val gradientShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "borderShift"
    )

    if (canhBao.value.CanhBaoRung) {
        Dialog(onDismissRequest = { /* Không cho tự tắt */ }) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(500)) + scaleIn(initialScale = 0.8f),
                exit = fadeOut(tween(500)) + scaleOut(targetScale = 0.8f)
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .border(
                            BorderStroke(
                                4.dp,
                                Brush.linearGradient(
                                    colors = listOf(Color.Red, Color.Yellow, Color.Red),
                                    start = androidx.compose.ui.geometry.Offset(0f, gradientShift),
                                    end = androidx.compose.ui.geometry.Offset(gradientShift, 0f)
                                )
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .background(blinkColor, RoundedCornerShape(20.dp)),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        val alarmAlpha by infiniteTransition.animateFloat(
                            initialValue = 1f,
                            targetValue = 0.3f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(500, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ), label = "alarmBlink"
                        )

                        Text(
                            text = "🚨 Cảnh báo an ninh 🚨",
                            fontSize = 22.sp,
                            color = Color.Red.copy(alpha = alarmAlpha),
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Có hành vi bất thường ở trạm\nVui lòng xác nhận an toàn.",
                            fontSize = 16.sp,
                            color = Color.White,
                            lineHeight = 22.sp
                        )

                        Button(
                            onClick = { baoRungViewModel.updateCanhBaoRung(false) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Xác nhận an toàn",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
