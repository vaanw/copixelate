package com.copixelate.art

private const val DEBUG_COLOR = 0x5e77b5 // Blue

internal class Palette {

    internal var colors: IntArray = IntArray(1) { DEBUG_COLOR }
        private set

    private var indexHistory: ArrayDeque<Int> = createIndexHistory()

    internal val activeIndex: Int get() = indexHistory[0]
    internal val activeColor: Int get() = colors[activeIndex]
    internal val priorActiveIndex: Int get() = indexHistory[1]

    internal val state: PixelRow get() = PixelRow(colors, activeIndex)

    internal fun select(index: Int) =
        indexHistory.apply { addFirst(index); removeLast() }

    internal fun remix(color: Int) = apply {
        colors[activeIndex] = color
    }

    internal fun resize(size: Int) = apply {
        colors = IntArray(size) { index ->
            when (index < colors.size) {
                true -> colors[index]
                false -> 0
            }
        }
    }

    internal fun clear(pixels: IntArray) = apply {
        colors = pixels
        indexHistory = createIndexHistory()
    }

    internal fun clear(mutator: (index: Int) -> Int) =
        clear(IntArray(colors.size, mutator))

    private fun createIndexHistory(): ArrayDeque<Int> =
        ArrayDeque(
            List(2) { index ->
                when (index < colors.size) {
                    true -> index
                    false -> colors.lastIndex
                }
            })

    // History
    //
    internal val history get() = historian.state
    private val historian = Historian()

    internal fun recordHistory(end: Boolean) = historian.recordHistory(colors, end)
    internal fun applyHistory(redo: Boolean) = apply {
        historian.applyHistory(colors, redo)
    }

}
