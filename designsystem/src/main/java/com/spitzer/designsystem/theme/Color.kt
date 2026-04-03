package com.spitzer.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class CoolRecipesColor(
    val n00n00: Color,
    val n00n99: Color,
    val n20n80: Color,
    val n30n30: Color,
    val n80n20: Color,
    val n80n80: Color,
    val n99n00: Color,
    val n99n99: Color,

    val p00p00: Color,
    val p10p10: Color,
    val p20p20: Color,
    val p90p90: Color,

    val s00s00: Color,
    val s10s10: Color,
    val s90s90: Color,

    val l00l00: Color,
    val l10l10: Color,

    val r00r00: Color
)

val CoolRecipesLightColor = CoolRecipesColor(
    n00n00 = Color(0, 0, 0),
    n00n99 = Color(0, 0, 0),
    n20n80 = Color(17, 20, 28),
    n30n30 = Color(130, 130, 130),
    n80n20 = Color(238, 235, 227),
    n80n80 = Color(238, 235, 227),
    n99n00 = Color(255, 255, 255),
    n99n99 = Color(255, 255, 255),

    // Primary (green)
    p00p00 = Color(95, 154, 92),
    p10p10 = Color(76, 125, 74),   // darker
    p20p20 = Color(120, 180, 115), // lighter
    p90p90 = Color(220, 240, 218), // very light background

    // Secondary (warm / food friendly)
    s00s00 = Color(210, 140, 70),  // base
    s10s10 = Color(170, 110, 55),  // darker
    s90s90 = Color(245, 220, 190), // light bg

    // Link (custom blue)
    l00l00 = Color(60, 120, 150),  // main link
    l10l10 = Color(40, 95, 120),   // pressed / visited

    r00r00 = Color(255, 54, 92)
)

val CoolRecipesDarkColor = CoolRecipesColor(
    n00n00 = Color(0, 0, 0),
    n00n99 = Color(255, 255, 255),
    n20n80 = Color(238, 235, 227),
    n30n30 = Color(130, 130, 130),
    n80n20 = Color(17, 20, 28),
    n80n80 = Color(238, 235, 227),
    n99n00 = Color(0, 0, 0),
    n99n99 = Color(255, 255, 255),

    // Primary
    p00p00 = Color(95, 154, 92),
    p10p10 = Color(120, 180, 115), // lighter (for dark mode emphasis)
    p20p20 = Color(76, 125, 74),   // darker
    p90p90 = Color(30, 60, 35),    // dark surface tint

    // Secondary
    s00s00 = Color(210, 140, 70),
    s10s10 = Color(240, 170, 100), // brighter for contrast
    s90s90 = Color(60, 40, 20),    // dark bg tint

    // Link (brighter for dark mode)
    l00l00 = Color(100, 170, 210),
    l10l10 = Color(140, 200, 235),

    r00r00 = Color(255, 84, 110)
)

val LocalColor = staticCompositionLocalOf {
    CoolRecipesLightColor
}
