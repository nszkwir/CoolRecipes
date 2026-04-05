package com.spitzer.designsystem.extensions

import android.graphics.Typeface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight

/**
 * Converts this [TextStyle] into a native Android [Typeface].
 *
 * This function utilizes the [LocalFontFamilyResolver] to resolve the typeface based on the
 * [fontFamily], [fontStyle], and [fontSynthesis] defined in the [TextStyle].
 *
 * @param fontWeight An optional [FontWeight] to override the weight defined in this [TextStyle].
 * If null, it defaults to the weight defined in the style, or [FontWeight.Normal] if unspecified.
 * @return The resolved [Typeface] instance.
 */
@Composable
fun TextStyle.toTypeface(fontWeight: FontWeight? = null): Typeface {
    val resolver: FontFamily.Resolver = LocalFontFamilyResolver.current
    val typeface: Typeface = remember(resolver, this) {
        resolver.resolve(
            fontFamily = this.fontFamily,
            fontWeight = fontWeight ?: FontWeight.Normal,
            fontStyle = this.fontStyle ?: FontStyle.Normal,
            fontSynthesis = this.fontSynthesis ?: FontSynthesis.All,
        )
    }.value as Typeface
    return typeface
}
