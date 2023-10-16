package com.copixelate.art

private const val HISTORY_LIMIT = 32

private typealias ChangePair = Pair<Int, Int>
private typealias ChangeMap = Map<Int, ChangePair>
private typealias HistoryDeque = ArrayDeque<ChangeMap>

internal class Historian {

    internal val state get() = HistoryAvailability(undoAvailable, redoAvailable)

    private val undoAvailable get() = historyIndex > 0
    private val redoAvailable get() = historyIndex < history.size

    private val history: HistoryDeque = ArrayDeque()
    private var previousData = IntArray(0)
    private var historyIndex = 0

    private var isRecording = false

    internal fun recordHistory(pixels: IntArray, end: Boolean) {
        when {
            !end && !isRecording -> {
                isRecording = true
                beginRecord(pixels)
            }
            end && isRecording -> {
                endRecord(pixels)
                isRecording = false
            }
            else -> throw IllegalStateException(
                "recordHistory: Desynchronization occurred:"
                        + " tried to ${if (end) "end" else "begin"} recording"
                        + " while ${if (isRecording) "already" else "not"} recording."
            )
        }
    }

    private fun beginRecord(pixels: IntArray) {
        previousData = pixels.copyOf()
    }

    private fun endRecord(pixels: IntArray) {
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

    internal fun applyHistory(pixels: IntArray, redo: Boolean) {
        when (redo) {
            false -> undoHistory(pixels)
            true -> redoHistory(pixels)
        }
    }

    private fun undoHistory(pixels: IntArray) {
        if (undoAvailable) {
            val changesMap = history[--historyIndex]
            applyChanges(changesMap, redo = false, pixels = pixels)
        }
    }

    private fun redoHistory(pixels: IntArray) {
        if (redoAvailable) {
            val changesMap = history[historyIndex]
            applyChanges(changesMap, redo = true, pixels = pixels)
            historyIndex++
        }
    }

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

}
