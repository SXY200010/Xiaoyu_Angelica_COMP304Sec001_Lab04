package com.example.xiaoyu_angelica_comp304sec001_lab04.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val OsakaFont = FontFamily.Default

val Typography = Typography(
    displayLarge = androidx.compose.ui.text.TextStyle(
        fontFamily = OsakaFont,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        letterSpacing = 0.5.sp
    ),
    headlineSmall = androidx.compose.ui.text.TextStyle(
        fontFamily = OsakaFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp
    ),
    titleMedium = androidx.compose.ui.text.TextStyle(
        fontFamily = OsakaFont,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    ),
    bodyMedium = androidx.compose.ui.text.TextStyle(
        fontFamily = OsakaFont,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp
    ),
    labelLarge = androidx.compose.ui.text.TextStyle(
        fontFamily = OsakaFont,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    )
)