package com.copixelate.art

internal class Palette {

    internal var colors: IntArray = IntArray(0) { 0 }
        private set
    internal var activeIndex: Int = 0
        private set

    internal val activeColor: Int get() = colors[activeIndex]
    internal val previousActiveIndex: Int get() = previousActiveIndexes[0]

    @SuppressWarnings("WeakerAccess")  // Here for potential future use
    internal var previousActiveIndexes = ArrayDeque<Int>(0)
        private set

    internal val state: PixelRow
        get() = PixelRow(colors, activeIndex)

    internal fun resize(size: Int) = apply {
        colors = IntArray(size) { index ->
            when (index < colors.size) {
                true -> colors[index]
                false -> 0
            }
        }
    }

    internal fun clear(mutator: (index: Int) -> Int) = apply {
        colors = IntArray(colors.size, mutator)
        previousActiveIndexes = ArrayDeque(
            List(3) { index ->
                colors.lastIndex - index
            })
    }

    internal fun clear(pixels: IntArray) = apply {
        colors = pixels
    }

    internal fun select(index: Int) {
        previousActiveIndexes.apply { addFirst(activeIndex); removeLast() }
        activeIndex = index
    }

}
