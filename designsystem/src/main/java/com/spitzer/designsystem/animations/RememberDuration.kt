package com.spitzer.designsystem.animations

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

@Composable
fun rememberDuration(
    value: Boolean,
    minDurationMillis: Long = 1_000L
): Boolean {
    var showLoading by remember { mutableStateOf(false) }
    var loadingStartTime by remember { mutableStateOf(0L) }

    LaunchedEffect(value) {
        if (value) {
            loadingStartTime = System.currentTimeMillis()
            showLoading = true
        } else {
            val elapsed = System.currentTimeMillis() - loadingStartTime
            val remaining = minDurationMillis - elapsed
            if (remaining > 0) delay(remaining)
            showLoading = false
        }
    }

    return showLoading
}
