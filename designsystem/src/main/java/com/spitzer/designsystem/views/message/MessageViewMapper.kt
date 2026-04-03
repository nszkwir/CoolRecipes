package com.spitzer.designsystem.views.message

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.spitzer.designsystem.R
import com.spitzer.designsystem.components.ActionButtonViewState
import com.spitzer.designsystem.data.AnnouncedAction

object MessageViewMapper {

    @Composable
    fun customMessage(
        subtitle: String,
        primaryButton: ActionButtonViewState,
        secondaryButton: ActionButtonViewState? = null,
    ) = MessageViewState(
        subtitle = subtitle,
        primaryButton = primaryButton,
        secondaryButton = secondaryButton
    )

    @Composable
    fun noInternetConnectionMessage(
        onPrimaryButtonClicked: () -> Unit,
        onSecondaryButtonClicked: (() -> Unit)? = null
    ) = MessageViewState(
        subtitle = stringResource(R.string.error_noInternet_subtitle),
        primaryButton = ActionButtonViewState(
            announcedAction = AnnouncedAction(
                action = onPrimaryButtonClicked,
                description = stringResource(R.string.try_again_title)
            )
        ),
        secondaryButton = onSecondaryButtonClicked?.let {
            ActionButtonViewState(
                announcedAction = AnnouncedAction(
                    action = onSecondaryButtonClicked,
                    description = stringResource(R.string.close)
                ),
                style = ActionButtonViewState.Style.WARNING
            )
        }
    )

    @Composable
    fun genericErrorMessage(
        onPrimaryButtonClicked: () -> Unit,
        onSecondaryButtonClicked: (() -> Unit)? = null
    ) = MessageViewState(
        subtitle = stringResource(R.string.error_generic_subtitle),
        primaryButton = ActionButtonViewState(
            announcedAction = AnnouncedAction(
                action = onPrimaryButtonClicked,
                description = stringResource(R.string.try_again_title)
            )
        ),
        secondaryButton = onSecondaryButtonClicked?.let {
            ActionButtonViewState(
                announcedAction = AnnouncedAction(
                    action = onSecondaryButtonClicked,
                    description = stringResource(R.string.close)
                ),
                style = ActionButtonViewState.Style.WARNING
            )
        }
    )
}
