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
import androidx.compose.ui.tooling.preview.Preview
import com.spitzer.designsystem.R
import com.spitzer.designsystem.animations.LottieAnimationView
import com.spitzer.designsystem.theme.CoolRecipesTheme

/**
 * A full-screen loading component that displays a centered Lottie animation.
 *
 * This view uses a transparent background and centers a specific loading animation
 * (R.raw.loading) within the provided [modifier] constraints.
 *
 * @param modifier The [Modifier] to be applied to the loading container.
 */
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

@Preview(showBackground = true)
@Composable
fun LoadingViewPreview() {
    CoolRecipesTheme {
        LoadingView()
    }
}
