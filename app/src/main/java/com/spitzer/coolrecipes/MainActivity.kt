package com.spitzer.coolrecipes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.spitzer.coolrecipes.navigation.RootCoordinator
import com.spitzer.designsystem.theme.CoolRecipesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val appViewModel: AppViewModel = hiltViewModel()
            val isDarkTheme by appViewModel.isDarkTheme.collectAsState()
            CoolRecipesTheme(darkTheme = isDarkTheme) {
                RootCoordinator()
            }
        }
    }
}
