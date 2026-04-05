package com.spitzer.ui.recipes.screen.list.views

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.spitzer.designsystem.R.raw.search_not_found
import com.spitzer.designsystem.animations.LottieAnimationView
import com.spitzer.designsystem.theme.CoolRecipesTheme

/**
 * A composable that displays a placeholder view when a search returns no results.
 * It features a Lottie animation to visually indicate the empty state and supports vertical scrolling.
 *
 * @param modifier The [Modifier] to be applied to the layout.
 */
@Composable
fun EmptySearchView(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        Column {
            LottieAnimationView(
                animation = search_not_found, modifier = Modifier.height(250.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Light Mode")
@Composable
fun PreviewEmptySearchView() {
    CoolRecipesTheme {
        EmptySearchView()
    }
}
