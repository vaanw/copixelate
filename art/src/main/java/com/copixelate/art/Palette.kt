package com.copixelate.art

internal class Palette {

    internal var size: Point = Point()
        private set
    internal var colors: IntArray = IntArray(0) { 0 }
        private set
    internal var activeIndex: Int = 0
        private set

    internal val activeColor: Int get() = colors[activeIndex]

    internal val previousActiveIndex: Int get() = previousActiveIndexes[0]
    @SuppressWarnings("WeakerAccess")  // Here for potential future use
    internal var previousActiveIndexes = ArrayDeque<Int>(0)
        private set

    val state: PixelGrid
        get() = PixelGrid(size, colors)
    val activeColorState: PixelGrid
        get() = PixelGrid(Point(1), IntArray(1) { activeColor })

    internal fun resize(size: Point) = apply {
        this.size = size
        colors = IntArray(size.area) { 0 }
    }

    internal fun clear(mutator: (index: Int) -> Int) = apply {
        colors = IntArray(size.area, mutator)
        previousActiveIndexes = ArrayDeque(
            List(3) { index -> colors.lastIndex - index })
    }

    internal fun clear(pixels: IntArray) = apply {
        colors = pixels
    }

    internal fun select(index: Int) {
        previousActiveIndexes.apply { addFirst(activeIndex); removeLast() }
        activeIndex = index
    }

}
