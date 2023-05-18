package com.copixelate.art

internal class Drawing {

    private var indexPixels = IntArray(0)
    private var colorPixels = IntArray(0)

    internal var size: Point = Point()
        private set

    val colorState get() = PixelGrid(colorPixels, size)
    val state get() = PixelGrid(indexPixels, size)

    internal val lastIndex get() = indexPixels.lastIndex

    internal fun resize(size: Point): Drawing = apply {
        this.size = size
        indexPixels = IntArray(size.area) { 0 }
    }

    internal fun recolor(colors: IntArray): Drawing = apply {
        colorPixels = IntArray(indexPixels.size) { i -> colors[indexPixels[i]] }
    }

    internal fun clear(paletteIndex: Int): Drawing = apply {
        for (i in indexPixels.indices) {
            indexPixels[i] = paletteIndex
        }
    }

    internal fun clear(mutator: (index: Int) -> Int) = apply {
        indexPixels = IntArray(size.area, mutator)
    }

    internal fun clear(pixels: IntArray) = apply {
        indexPixels = pixels
    }

    internal fun draw(index: Int, pixelIndex: Int, pixelColor: Int) = apply {
        indexPixels[index] = pixelIndex
        colorPixels[index] = pixelColor
    }

    internal fun draw(indexes: IntArray, pixelIndex: Int, pixelColor: Int) {
        indexes.forEach { index ->
            draw(index, pixelIndex, pixelColor)
        }
    }

}
