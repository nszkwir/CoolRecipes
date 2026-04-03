package com.spitzer.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.spitzer.designsystem.R
import com.spitzer.designsystem.animations.LottieAnimationView

@Composable
fun LoadingView(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimationView(
            modifier = Modifier.size(width = 100.dp, height = 100.dp),
            animation = R.raw.loading,
            shouldLoop = true
        )
    }
}
