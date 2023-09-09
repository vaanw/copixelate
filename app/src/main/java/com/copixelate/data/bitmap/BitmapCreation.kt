package com.copixelate.data.bitmap

import android.graphics.Bitmap
import com.copixelate.data.model.DrawingModel

val BITMAP_CONFIG = Bitmap.Config.ARGB_8888

fun createBitmap(colors: IntArray, width: Int, height: Int): Bitmap =
    Bitmap.createBitmap(colors, width, height, BITMAP_CONFIG)

private fun createBitmap(colorDrawingModel: DrawingModel): Bitmap =
    colorDrawingModel.run {
        createBitmap(pixels.toIntArray(), size.x, size.y)
    }

private fun createScaledBitmap(src: Bitmap, scaledWidth: Int, scaledHeight: Int): Bitmap =
    Bitmap.createScaledBitmap(src, scaledWidth, scaledHeight, false)

fun createScaledBitmap(colorDrawingModel: DrawingModel, scaleFactor: Int): Bitmap =
    colorDrawingModel.run {
        createScaledBitmap(
            src = createBitmap(colorDrawingModel = this),
            scaledWidth = scaleFactor * size.x,
            scaledHeight = scaleFactor * size.y,
        )
    }
