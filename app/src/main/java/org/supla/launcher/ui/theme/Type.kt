package org.supla.launcher.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.supla.launcher.R

object SuplaFont {
    val OpenSans = FontFamily(
        Font(R.font.open_sans_light, FontWeight.Light),
        Font(R.font.open_sans_regular, FontWeight.Normal),
        Font(R.font.open_sans_bold, FontWeight.Bold),
        Font(R.font.open_sans_italic, FontWeight.Normal, style = FontStyle.Italic),
        Font(R.font.open_sans_italic_light, FontWeight.Light, style = FontStyle.Italic)
    )
}

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = SuplaFont.OpenSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = SuplaFont.OpenSans,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodySmall = TextStyle(
        fontFamily = SuplaFont.OpenSans,
        fontWeight = FontWeight.Light,
        fontStyle = FontStyle.Italic,
        fontSize = 11.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = SuplaFont.OpenSans,
        fontWeight = FontWeight.Normal,
        fontSize = 48.sp,
        letterSpacing = 0.5.sp
    ),
    titleSmall = TextStyle(
        fontFamily = SuplaFont.OpenSans,
        fontWeight = FontWeight.Normal,
        fontSize = 48.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
titleLarge = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = 22.sp,
    lineHeight = 28.sp,
    letterSpacing = 0.sp
),
labelSmall = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Medium,
    fontSize = 11.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.5.sp
)
*/
)