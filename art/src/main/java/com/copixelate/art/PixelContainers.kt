package com.copixelate.art

class PixelGrid(
    val pixels: IntArray,
    val size: Point
)

class PixelRow(
    val pixels: IntArray,
    val activeIndex: Int = 0
) {
    val activeColor
        get() = pixels[activeIndex]
}

class PixelUpdate(
    val key: Int,
    val value: Int
)
