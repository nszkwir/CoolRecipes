package com.spitzer.data.remote.recipe.factory

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType

object JsonConverterFactory {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    fun getJsonConverter() = json.asConverterFactory(MediaType.get("application/json"))
}
