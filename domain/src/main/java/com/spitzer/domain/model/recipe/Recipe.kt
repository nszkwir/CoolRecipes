package com.spitzer.domain.model.recipe

import java.net.URL

data class Recipe(
    val id: Long,
    val title: String,
    val image: URL?,
    val summary: String
)
