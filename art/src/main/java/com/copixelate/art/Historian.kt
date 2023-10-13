package com.copixelate.art

private const val HISTORY_LIMIT = 16

private typealias ChangePair = Pair<Int, Int>
private typealias ChangeMap = Map<Int, ChangePair>
private typealias HistoryDeque = ArrayDeque<ChangeMap>

internal class Historian {

    internal val undoAvailable get() = historyIndex > 0
    internal val redoAvailable get() = historyIndex < history.size

    private val history: HistoryDeque = ArrayDeque()
    private var previousData = IntArray(0)
    private var historyIndex = 0

    private fun applyChanges(
        changeMap: ChangeMap,
        redo: Boolean = false,
        pixels: IntArray
    ): IntArray {
        changeMap.forEach { (index, pair) ->
            val newValue = when (redo) {
                false -> pair.first
                true -> pair.second
            }
            pixels[index] = newValue
        }
        return pixels
    }

    internal fun beginRecord(pixels: IntArray) {
        previousData = pixels.copyOf()
    }

    internal fun endRecord(pixels: IntArray) {
        val changeMap = mutableMapOf<Int, Pair<Int, Int>>()

        for ((index, previousValue) in previousData.withIndex()) {
            val newValue = pixels[index]
            if (previousValue != newValue) {
                changeMap[index] = Pair(previousValue, newValue)
            }
        }

        if (changeMap.isNotEmpty()) {
            while (history.size > historyIndex) {
                history.removeLast()
            }

            history.addLast(changeMap)
            historyIndex++

            if (history.size > HISTORY_LIMIT) {
                history.removeFirst()
                historyIndex--
            }
        }
    }

    internal fun undoHistory(pixels: IntArray) {
        if (undoAvailable) {
            val changesMap = history[--historyIndex]
            applyChanges(changesMap, redo = false, pixels = pixels)
        }
    }

    internal fun redoHistory(pixels: IntArray) {
        if (redoAvailable) {
            val changesMap = history[historyIndex]
            applyChanges(changesMap, redo = true, pixels = pixels)
            historyIndex++
        }
    }

}
