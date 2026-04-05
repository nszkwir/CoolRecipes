package com.spitzer.designsystem.views.message

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.spitzer.designsystem.R
import com.spitzer.designsystem.animations.LottieAnimationView
import com.spitzer.designsystem.components.ActionButtonView
import com.spitzer.designsystem.components.ActionButtonViewState
import com.spitzer.designsystem.data.AnnouncedAction
import com.spitzer.designsystem.extensions.shortToast
import com.spitzer.designsystem.theme.CoolRecipesTheme
import com.spitzer.designsystem.theme.Spacing

data class MessageViewState(
    val subtitle: String,
    val animation: Int = R.raw.error,
    val primaryButton: ActionButtonViewState,
    val secondaryButton: ActionButtonViewState? = null
)

/**
 * A full-screen informational view used to display messages, errors, or empty states.
 *
 * This component displays a descriptive subtitle, a Lottie animation for visual feedback,
 * and up to two action buttons (primary and optional secondary) positioned at the bottom.
 *
 * @param modifier The [Modifier] to be applied to the layout.
 * @param viewState The state object containing the subtitle text, animation resource, and button configurations.
 */
@Composable
fun MessageView(
    modifier: Modifier = Modifier,
    viewState: MessageViewState
) {
    val colors = CoolRecipesTheme.colors

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.n99n00)
            .padding(Spacing.FOUR.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = viewState.subtitle,
                textAlign = TextAlign.Center,
                color = colors.n00n99,
                style = CoolRecipesTheme.typography.body1,
                modifier = Modifier.padding(bottom = Spacing.FOUR.dp)
            )

            LottieAnimationView(
                animation = viewState.animation,
                modifier = Modifier.height(250.dp)
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Spacing.THREE.dp)
        ) {
            viewState.secondaryButton?.let {
                ActionButtonView(
                    modifier = Modifier.fillMaxWidth(),
                    viewState = it
                )
            }

            ActionButtonView(
                modifier = Modifier.fillMaxWidth(),
                viewState = viewState.primaryButton
            )
        }
    }
}

@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Light Mode")
@Composable
fun PreviewMessageView() {
    val context = LocalContext.current
    CoolRecipesTheme {
        Surface(color = CoolRecipesTheme.colors.n99n00) {
            MessageView(
                modifier = Modifier.fillMaxSize(),
                viewState = MessageViewState(
                    subtitle = "Error subtitle",
                    primaryButton = ActionButtonViewState(
                        announcedAction = AnnouncedAction(
                            description = "Retry",
                            action = {context.shortToast("Retry")})
                    ),
                    secondaryButton = ActionButtonViewState(
                        announcedAction = AnnouncedAction(
                            description = "Cancel",
                            action = {context.shortToast("Cancel")}),
                        style = ActionButtonViewState.Style.WARNING
                    ),
                )
            )
        }
    }
}
