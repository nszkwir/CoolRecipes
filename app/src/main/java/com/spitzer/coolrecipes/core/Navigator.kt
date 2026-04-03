package com.spitzer.coolrecipes.core

import androidx.navigation3.runtime.NavKey

class Navigator(val state: NavigationState) {

    fun navigate(route: NavKey) {
        if (route in state.backStacks.keys) {
            state.topLevelRoute = route
        } else {
            state.backStacks[state.topLevelRoute]?.add(route)
        }
    }

    fun goBack() {
        state.backStacks.getOrDefault(
            state.topLevelRoute,
            null
        )?.let { currentStack ->
            val currentRoute = currentStack.last()
            if (currentRoute == state.topLevelRoute) {
                state.topLevelRoute = state.startRoute
            } else {
                currentStack.removeLastOrNull()
            }
        }
    }
}
