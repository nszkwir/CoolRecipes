package com.spitzer.designsystem.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class Spacing(private val value: Int) {
    HALF(1),
    ONE(2),
    TWO(4),
    THREE(8),
    FOUR(16),
    FIVE(24),
    SIX(32);

    val dp: Dp
        get() = value.dp
}

enum class BorderRadius(private val value: Int) {
    HALF(2),
    ONE(4),
    TWO(6),
    THREE(8),
    FOUR(12),
    FIVE(16),
    SIX(18),
    SEVEN(24),
    EIGHT(32),
    NINE(40);

    val dp: Dp
        get() = value.dp
}