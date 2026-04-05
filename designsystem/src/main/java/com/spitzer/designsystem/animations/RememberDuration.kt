package com.spitzer.designsystem.animations

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

/**
 * Remembers a boolean state that remains `true` for at least a specified [minDurationMillis].
 *
 * This is commonly used to prevent UI "flickering" (e.g., a loading spinner appearing and
 * disappearing too quickly) by ensuring that the active state is visible for a minimum
 * amount of time, even if the source [value] returns to `false` sooner.
 *
 * @param value The input trigger state (e.g., an "isLoading" flag).
 * @param minDurationMillis The minimum time in milliseconds the returned state should stay `true`.
 * @return A delayed boolean state that respects the minimum duration.
 */
@Composable
fun rememberDuration(
    value: Boolean,
    minDurationMillis: Long = 1_000L
): Boolean {
    var showLoading by remember { mutableStateOf(false) }
    var loadingStartTime by remember { mutableLongStateOf(0L) }

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
