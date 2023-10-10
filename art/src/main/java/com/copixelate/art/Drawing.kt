package com.copixelate.art

private const val DEBUG_COLOR = 0x845eb5 // Purple
private const val HISTORY_LIMIT = 16

internal class Drawing {

    private var indexPixels = IntArray(1) { 0 }
    private var colorPixels = IntArray(1) { DEBUG_COLOR }

    internal var size: Point = Point(1)
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

    // History
    //
    private val history: ArrayDeque<Map<Int, Pair<Int, Int>>> = ArrayDeque()
    private var historicState = state.copy()
    private var historyIndex = 0

    internal val undoAvailable get() = historyIndex > 0
    internal val redoAvailable get() = historyIndex < history.size

    private fun applyChanges(
        changeMap: Map<Int, Pair<Int, Int>>,
        redo: Boolean = false
    ) = apply {
        changeMap.forEach { (index, pair) ->
            val newValue = when (redo) {
                false -> pair.first
                true -> pair.second
            }
            indexPixels[index] = newValue
        }
    }

    internal fun recordHistoricState() {
        historicState = state.copy()
    }

    internal fun recordHistory() {
        val changeMap = mutableMapOf<Int, Pair<Int, Int>>()

        // Scan for and record changes
        for ((index, historicValue) in historicState.pixels.withIndex()) {
            val newValue = state.pixels[index]
            if (historicValue != newValue) {
                changeMap[index] = Pair(historicValue, newValue)
            }
        }

        if (changeMap.isNotEmpty()) {
            // Remove all future history
            while (history.size > historyIndex) {
                history.removeLast()
            }

            // Add these changes to history
            history.addLast(changeMap)
            historyIndex++

            // Limit history size
            if (history.size > HISTORY_LIMIT) {
                history.removeFirst()
                historyIndex--
            }
        }
    }

    internal fun undoHistory() = apply {
        if (undoAvailable) {
            val changesMap = history[--historyIndex]
            applyChanges(changesMap)
        }
    }

    internal fun redoHistory() = apply {
        if (redoAvailable) {
            val changesMap = history[historyIndex]
            applyChanges(changesMap, true)
            historyIndex++
        }
    }

}
