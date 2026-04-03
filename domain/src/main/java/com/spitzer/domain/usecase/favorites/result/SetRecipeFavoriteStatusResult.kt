package com.spitzer.domain.usecase.favorites.result

sealed class SetRecipeFavoriteStatusResult {
    data object Success : SetRecipeFavoriteStatusResult()
    data object Error : SetRecipeFavoriteStatusResult()
}