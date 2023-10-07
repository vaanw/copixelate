package com.copixelate.art

class PixelGrid(
    val pixels: IntArray,
    val size: Point
){
    val aspectRatio: Float
        get() = size.x * 1f / size.y
}

class PixelRow(
    val pixels: IntArray,
    val activeIndex: Int = 0
) {
    val activeColor: Int
        get() = pixels[activeIndex]
}

class PixelUpdate(
    val key: Int,
    val value: Int
)
