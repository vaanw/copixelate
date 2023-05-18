package com.copixelate.art

import kotlin.random.Random

private const val DEFAULT_DRAWING_WIDTH = 32
private const val DEFAULT_DRAWING_HEIGHT = 32
private const val DEFAULT_PALETTE_SIZE = 7

private const val DEFAULT_BRUSH_SIZE = 7
private val DEFAULT_BRUSH_STYLE = Brush.Style.CIRCLE


class ArtSpace {

    val state = PublicState(this)

    class PublicState(private val space: ArtSpace) {
        val colorDrawing: PixelGrid
            get() = space.drawing.colorState
        val drawing: PixelGrid
            get() = space.drawing.state
        val palette: PixelRow
            get() = space.palette.state
        val brushPreview: PixelGrid
            get() = space.brushPreview.colorState
        val brushSize get() = space.brush.size
    }

    private val palette = createDefaultPalette()
    private val brush = createDefaultBrush()
    private val drawing = createDefaultDrawing()

    private val brushPreview = Drawing()

    init {
        refreshBrushPreviewBitmap()
    }

    private fun refreshBrushPreviewBitmap() {

        var previewSize = drawing.size / 3

        if (previewSize.x <= brush.size)
            previewSize = Point(brush.size + 1, brush.size + 1)

        // Center by ensuring size parity
        if (previewSize.x % 2 != brush.size % 2)
            previewSize += 1

        val drawPosition = previewSize / 2f
        val drawIndexes = brush
            .toPointsAt(drawPosition)
            .toIndexes(previewSize)

        brushPreview
            .resize(previewSize)
            .clear(palette.previousActiveIndex)
            .recolor(palette.colors)
            .draw(
                indexes = drawIndexes,
                pixelIndex = palette.activeIndex,
                pixelColor = palette.activeColor
            )
    }

    fun updateBrushSize(size: Int) {
        brush.restyle(size = size)
        refreshBrushPreviewBitmap()
    }

    fun clear(drawingState: PixelGrid, paletteState: PixelRow): ArtSpaceResult<Unit> {

        if (drawingState.pixels.max() > paletteState.pixels.lastIndex) {
            return ArtSpaceResult.Failure(
                IllegalArgumentException(
                    "clear: drawingState.pixels.max() ${drawingState.pixels.max()} exceeds paletteState.pixels.lastIndex ${paletteState.pixels.lastIndex}"
                )
            )
        }

        val ogPaletteState = state.palette

        clearPalette(paletteState).apply {
            if (isFailure) return this
        }

        clearDrawing(drawingState).apply {
            if (isFailure) {
                // Restore palette state since we got a bad drawing
                clearPalette(ogPaletteState)
                return this
            }
        }

        refreshBrushPreviewBitmap()

        return ArtSpaceResult.Success(Unit)
    }

    private fun clearDrawing(drawingState: PixelGrid): ArtSpaceResult<Unit> {

        if (drawingState.size.area != drawingState.pixels.size) {
            return ArtSpaceResult.Failure(
                IllegalArgumentException(
                    "clearDrawing: drawingState.size.area ${drawingState.size.area} doesn't match drawingState.pixels.size ${drawingState.pixels.size}"
                )
            )
        }

        drawing
            .resize(drawingState.size)
            .clear(drawingState.pixels)
            .recolor(palette.colors)

        return ArtSpaceResult.Success(Unit)
    }

    private fun clearPalette(paletteState: PixelRow): ArtSpaceResult<Unit> =
        ArtSpaceResult.Success(Unit).also {
            palette.clear(paletteState.pixels)
        }

    fun updateDrawing(update: PixelUpdate): ArtSpaceResult<Unit> {

        if (update.key > drawing.lastIndex) {
            return ArtSpaceResult.Failure(
                IllegalArgumentException(
                    "updateDrawing: update.key ${update.key} exceeds drawing.lastIndex ${drawing.lastIndex}"
                )
            )
        }

        if (update.value > palette.colors.lastIndex) {
            return ArtSpaceResult.Failure(
                IllegalArgumentException(
                    "updateDrawing: update.value ${update.value} exceeds palette.colors.lastIndex ${palette.colors.lastIndex}"
                )
            )
        }

        drawing.draw(
            index = update.key,
            pixelIndex = update.value,
            pixelColor = palette.colors[update.value]
        )

        return ArtSpaceResult.Success(Unit)
    }

    fun updateDrawing(unitPosition: PointF): ArtSpaceResult<Unit> {

        if (!unitPosition.isUnit()) {
            return ArtSpaceResult.Failure(
                IllegalArgumentException(
                    "updateDrawing: unitPosition $unitPosition exceeds ${PointF(1f)}"
                )
            )
        }

        val drawPosition = unitPosition * drawing.size

        drawing.draw(
            indexes = brush
                .toPointsAt(drawPosition)
                .toIndexes(drawing.size),
            pixelIndex = palette.activeIndex,
            pixelColor = palette.activeColor
        )

        return ArtSpaceResult.Success(Unit)
    }

    fun updatePaletteActiveIndex(paletteIndex: Int): ArtSpaceResult<Unit> {

        if (paletteIndex == palette.activeIndex) {
            return ArtSpaceResult.Failure(
                IllegalArgumentException(
                    "updatePaletteActiveIndex: Failed; paletteIndex $paletteIndex is equal to palette.activeIndex ${palette.activeIndex}"
                )
            )
        }

        palette.select(paletteIndex)
        refreshBrushPreviewBitmap()

        return ArtSpaceResult.Success(Unit)
    }

    private fun createDefaultPalette() =
        Palette()
            .resize(size = DEFAULT_PALETTE_SIZE)
            .clear { Random(System.nanoTime()).nextInt() }

    private fun createDefaultDrawing() =
        Drawing()
            .resize(Point(DEFAULT_DRAWING_WIDTH, DEFAULT_DRAWING_HEIGHT))
            .clear { index: Int -> (index / 5) % palette.colors.size }
            .recolor(palette.colors)

    private fun createDefaultBrush() =
        Brush()
            .restyle(DEFAULT_BRUSH_STYLE, DEFAULT_BRUSH_SIZE)

}
