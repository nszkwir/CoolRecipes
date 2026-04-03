package com.spitzer.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.spitzer.designsystem.theme.CoolRecipesTheme
import com.spitzer.designsystem.theme.Spacing

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {
    val viewModel: SettingsScreenViewModel = hiltViewModel()
    val viewState by viewModel.viewState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CoolRecipesTheme.colors.n99n00)
            .padding(Spacing.FOUR.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.settings_dark_mode),
                color = CoolRecipesTheme.colors.n00n99,
                style = CoolRecipesTheme.typography.body1
            )
            Switch(
                checked = viewState.isDarkTheme,
                onCheckedChange = { viewModel.setDarkTheme(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = CoolRecipesTheme.colors.p00p00,
                    checkedTrackColor = CoolRecipesTheme.colors.p90p90,
                    uncheckedThumbColor = CoolRecipesTheme.colors.n30n30,
                    uncheckedTrackColor = CoolRecipesTheme.colors.n80n20,
                    checkedBorderColor = CoolRecipesTheme.colors.p00p00,
                    uncheckedBorderColor = CoolRecipesTheme.colors.n30n30
                )
            )
        }
    }
}
