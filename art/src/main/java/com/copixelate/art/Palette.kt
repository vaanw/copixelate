package com.copixelate.art

private const val DEBUG_COLOR = 0x5e77b5 // Blue
private const val HISTORY_LIMIT = 16

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
    private val history: ArrayDeque<Map<Int, Pair<Int, Int>>> = ArrayDeque()
    private var historicState = colors.copyOf()
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
            colors[index] = newValue
        }
    }

    internal fun recordHistoricState() {
        historicState = colors.copyOf()
    }

    internal fun recordHistory() {
        val changeMap = mutableMapOf<Int, Pair<Int, Int>>()

        // Scan for and record changes
        for ((index, historicValue) in historicState.withIndex()) {
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
