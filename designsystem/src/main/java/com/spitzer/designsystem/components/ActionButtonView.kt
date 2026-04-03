package com.spitzer.designsystem.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.spitzer.designsystem.data.AnnouncedAction
import com.spitzer.designsystem.extensions.shortToast
import com.spitzer.designsystem.theme.BorderRadius
import com.spitzer.designsystem.theme.CoolRecipesTheme
import com.spitzer.designsystem.theme.Spacing

data class ActionButtonViewState(
    val announcedAction: AnnouncedAction,
    val style: Style = Style.PRIMARY,
    val isEnabled: Boolean = true
) {
    enum class Style {
        PRIMARY, WARNING
    }
}

@Composable
fun ActionButtonView(
    modifier: Modifier = Modifier,
    viewState: ActionButtonViewState
) {
    Button(
        onClick = viewState.announcedAction.action,
        enabled = viewState.isEnabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (viewState.style == ActionButtonViewState.Style.PRIMARY) CoolRecipesTheme.colors.p00p00 else CoolRecipesTheme.colors.r00r00,
            disabledContainerColor = CoolRecipesTheme.colors.n30n30
        ),
        shape = RoundedCornerShape(BorderRadius.TWO.dp),
        contentPadding = PaddingValues(
            horizontal = Spacing.FOUR.dp,
            vertical = Spacing.FOUR.dp
        ),
        modifier = modifier
    ) {
        Text(
            text = viewState.announcedAction.description,
            textAlign = TextAlign.Center,
            color = CoolRecipesTheme.colors.n99n99,
            style = CoolRecipesTheme.typography.button,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Light Mode")
@Composable
fun PreviewActionButtonView() {
    val context = LocalContext.current
    CoolRecipesTheme {
        Surface(color = CoolRecipesTheme.colors.n99n00) {
            Column(
                modifier = Modifier.padding(Spacing.THREE.dp),
                verticalArrangement = Arrangement.spacedBy(Spacing.THREE.dp)
            ) {
                ActionButtonView(
                    viewState = ActionButtonViewState(
                        style = ActionButtonViewState.Style.PRIMARY,
                        announcedAction = AnnouncedAction({
                            context.shortToast("PRIMARY")
                        }, "PRIMARY")
                    )
                )
                ActionButtonView(
                    viewState = ActionButtonViewState(
                        style = ActionButtonViewState.Style.WARNING,
                        announcedAction = AnnouncedAction({
                            context.shortToast("WARNING")
                        }, "WARNING")
                    )
                )
            }
        }
    }
}
