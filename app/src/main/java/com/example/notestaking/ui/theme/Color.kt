package com.example.notestaking.ui.theme

import androidx.compose.ui.graphics.Color

val NovaIndigo = Color(0xFF5C6BC0)
val NovaIndigoDark = Color(0xFF3949AB)
val NovaTeal = Color(0xFF26A69A)
val NovaTealDark = Color(0xFF00897B)
val NovaSurfaceLight = Color(0xFFF8F9FC)
val NovaSurfaceDark = Color(0xFF12141C)
val NovaCardLight = Color(0xFFFFFFFF)
val NovaCardDark = Color(0xFF1E2230)

val NoteColorDefault = Color(0xFFE8EAF6)
val NoteColorRose = Color(0xFFFCE4EC)
val NoteColorAmber = Color(0xFFFFF8E1)
val NoteColorMint = Color(0xFFE0F2F1)
val NoteColorSky = Color(0xFFE3F2FD)
val NoteColorLavender = Color(0xFFEDE7F6)

val NoteColorDefaultDark = Color(0xFF2A2D3E)
val NoteColorRoseDark = Color(0xFF3D2A32)
val NoteColorAmberDark = Color(0xFF3D3628)
val NoteColorMintDark = Color(0xFF243532)
val NoteColorSkyDark = Color(0xFF24303D)
val NoteColorLavenderDark = Color(0xFF2E2A3D)

fun noteCategoryColor(category: Int, darkTheme: Boolean): Color = when (category) {
    1 -> if (darkTheme) NoteColorRoseDark else NoteColorRose
    2 -> if (darkTheme) NoteColorAmberDark else NoteColorAmber
    3 -> if (darkTheme) NoteColorMintDark else NoteColorMint
    4 -> if (darkTheme) NoteColorSkyDark else NoteColorSky
    5 -> if (darkTheme) NoteColorLavenderDark else NoteColorLavender
    else -> if (darkTheme) NoteColorDefaultDark else NoteColorDefault
}
