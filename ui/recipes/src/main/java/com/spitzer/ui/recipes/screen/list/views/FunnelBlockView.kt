package com.spitzer.ui.recipes.screen.list.views

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.spitzer.designsystem.components.PillView
import com.spitzer.designsystem.components.PillViewState
import com.spitzer.designsystem.theme.CoolRecipesTheme
import com.spitzer.designsystem.theme.Spacing

/**
 * A composable that displays a titled section containing a horizontal row of selectable pills.
 *
 * @param modifier The [Modifier] to be applied to the root layout.
 * @param title The header text displayed above the horizontal list.
 * @param pillsViewStates A list of [PillViewState] objects representing the state and behavior of each pill.
 */
@Composable
fun FunnelBlockView(
    modifier: Modifier = Modifier,
    title: String,
    pillsViewStates: List<PillViewState>,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.THREE.dp)
    ) {
        Text(
            text = title,
            color = CoolRecipesTheme.colors.n00n00,
            style = CoolRecipesTheme.typography.heading3,
            fontWeight = FontWeight.Bold
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.FOUR.dp)
        ) {
            items(pillsViewStates.count()) { index ->
                PillView(viewState = pillsViewStates[index])
            }
        }
    }
}

@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Light Mode")
@Composable
fun PreviewFunnelBlockView() {
    CoolRecipesTheme {
        FunnelBlockView(
            title = "TITLE",
            pillsViewStates = listOf(
                PillViewState(
                    text = "PILL 1",
                    isSelected = true,
                    onSelectionChange = {}
                ),
                PillViewState(
                    text = "PILL 2",
                    isSelected = false,
                    onSelectionChange = {}
                ),
                PillViewState(
                    text = "PILL 3",
                    isSelected = false,
                    onSelectionChange = {}
                )
            )
        )
    }
}
