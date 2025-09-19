package com.tramites1cero1.centralizacion.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.tramites1cero1.centralizacion.R

val RobotoBold = FontFamily(
    Font(R.font.roboto_bold, weight = FontWeight.Normal)
)

val Roboto_bold_condensed = FontFamily(
    Font(R.font.roboto_condensed_bold, weight = FontWeight.Normal)
)

val Roboto_medium = FontFamily(
    Font(R.font.roboto_medium)
)

val Roboto_semiBold = FontFamily(
    Font(R.font.roboto_semi_bold, weight = FontWeight.Normal)
)

val Roboto_regular = FontFamily(
    Font(R.font.roboto_regular, weight = FontWeight.Normal)
)

val Typography = Typography(
    titleLarge = TextStyle(
        fontFamily = RobotoBold,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.2.sp
    ),

    titleMedium = TextStyle(
        fontFamily = RobotoBold,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.2.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = RobotoBold,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.2.sp
    ),

    headlineSmall = TextStyle(
        fontFamily = RobotoBold,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.1.sp
    ),

    bodyLarge = TextStyle(
        fontFamily = Roboto_medium,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Roboto_regular,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.2.sp
    ),
    bodySmall = TextStyle(
        fontFamily = Roboto_medium,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.2.sp
    )
)