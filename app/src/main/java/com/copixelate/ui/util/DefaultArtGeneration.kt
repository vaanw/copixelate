package com.copixelate.ui.util

import android.graphics.Color
import com.copixelate.data.model.DrawingModel
import com.copixelate.data.model.PaletteModel
import com.copixelate.data.model.SizeModel
import com.copixelate.data.model.SpaceModel
import kotlin.random.Random

private const val DEFAULT_DRAWING_WIDTH = 32
private const val DEFAULT_DRAWING_HEIGHT = 32
private const val DEFAULT_PALETTE_SIZE = 6

fun SpaceModel.createDefaultArt(
    width: Int = DEFAULT_DRAWING_WIDTH,
    height: Int = DEFAULT_DRAWING_HEIGHT,
    paletteSize: Int = DEFAULT_PALETTE_SIZE
) = copy(
    palette = PaletteModel().createDefaultArt(paletteSize),
    drawing = DrawingModel().createDefaultArt(width, height, paletteSize)
)

private fun PaletteModel.createDefaultArt(
    size: Int
) = copy(
    pixels = List(
        size = size,
        init = {
            randomOpaque()
        }
    )
)

private fun DrawingModel.createDefaultArt(
    width: Int,
    height: Int,
    paletteSize: Int
) = copy(
    size = SizeModel(x = width, y = height),
    pixels = List(
        size = width * height,
        init = { index: Int ->
            (index / 5) % paletteSize
        }
    )
)

private const val ARGB_MAX = 255

private fun randomOpaque(): Int {
    val rand: Int = Random(System.nanoTime()).nextInt()
    return Color.argb(
        ARGB_MAX,
        Color.red(rand),
        Color.green(rand),
        Color.blue(rand)
    )
}
