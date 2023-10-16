package com.copixelate.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.copixelate.art.ArtSpace
import com.copixelate.art.ArtSpaceResult
import com.copixelate.art.PointF
import com.copixelate.data.model.SpaceModel
import com.copixelate.data.model.copyFrom
import com.copixelate.data.model.toArtSpace
import com.copixelate.data.model.toModel
import com.copixelate.data.repo.ArtRepo
import com.copixelate.data.repo.UiRepo
import com.copixelate.ui.screens.art.TouchStatus
import com.copixelate.ui.screens.art.TransformState
import com.copixelate.ui.util.generateDefaultArt
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

const val DEFAULT_BRUSH_SIZE = 1
const val DEFAULT_TRANSFORM_ENABLED = true

class ArtViewModel : ViewModel() {

    private val artRepo = ArtRepo
    private val uiRepo = UiRepo

    private val _contentReady = MutableStateFlow(false)
    val contentReady = _contentReady.asStateFlow()

    private var artSpace = ArtSpace()
    private var spaceModel = SpaceModel()

    private val _drawing = MutableStateFlow(artSpace.state.colorDrawing)
    private val _palette = MutableStateFlow(artSpace.state.palette.toModel())
    private val _brushPreview = MutableStateFlow(artSpace.state.brushPreview)
    private val _brushSize = MutableStateFlow(DEFAULT_BRUSH_SIZE)

    val drawing = _drawing.asStateFlow()
    val palette = _palette.asStateFlow()
    val brushPreview = _brushPreview.asStateFlow()
    val brushSize = _brushSize.asStateFlow()

    private var _transformState = TransformState()
    val transformState get() = _transformState

    private val _transformEnabled = MutableStateFlow(DEFAULT_TRANSFORM_ENABLED)
    val transformEnabled = _transformEnabled.asStateFlow()

    private val _drawingHistory = MutableStateFlow(artSpace.state.drawingHistory)
    val drawingHistory = _drawingHistory.asStateFlow()

    private val _paletteHistory = MutableStateFlow(artSpace.state.paletteHistory)
    val paletteHistory = _paletteHistory.asStateFlow()

    private val _historyExpanded = MutableStateFlow(false)
    val historyExpanded = _historyExpanded.asStateFlow()


    init {
        // Determine appropriate SpaceModel to display
        viewModelScope.launch {
            @OptIn(ExperimentalCoroutinesApi::class)
            // Find SpaceModel using currentId
            uiRepo.currentSpaceIdFlow()
                .flatMapLatest { currentId ->
                    _contentReady.update { false }
                    artRepo.spaceByIdFlow(id = currentId)
                }
                .collect { newSpaceModel: SpaceModel? ->

                    if (newSpaceModel?.id == spaceModel.id) return@collect

                    newSpaceModel
                        // if SpaceModel is found, refresh
                        ?.let { value: SpaceModel ->
                            spaceModel = value
                            refreshArtSpace(newArtSpace = value.toArtSpace())
                            _contentReady.update { true }
                        }
                    // if SpaceModel is not found, find a default
                        ?: artRepo.getDefaultSpace()
                            ?.also { value: SpaceModel ->
                                uiRepo.saveCurrentSpaceId(spaceId = value.id.localId)
                            }
                        // if a default SpaceModel is not found, create a new one
                        ?: run {
                            val newId = artRepo.saveSpace(
                                spaceModel = SpaceModel().generateDefaultArt()
                            )
                            uiRepo.saveCurrentSpaceId(spaceId = newId)
                        }

                } // End collect()
        } // End viewModelScope.launch()
    } // End init()

    private fun refreshArtSpace(newArtSpace: ArtSpace) {
        artSpace = newArtSpace.apply {
            updateBrushSize(DEFAULT_BRUSH_SIZE)
        }
        _brushSize.update { artSpace.state.brushSize }
        _drawing.update { artSpace.state.colorDrawing }
        _palette.update { artSpace.state.palette.toModel() }
        _brushPreview.update { artSpace.state.brushPreview }
        _drawingHistory.update { artSpace.state.drawingHistory }
        _paletteHistory.update { artSpace.state.paletteHistory }
        _transformState = TransformState()
        _transformEnabled.update { DEFAULT_TRANSFORM_ENABLED }
    }

    private suspend inline fun ArtSpace.save() {
        val artSpaceToSave = this
        viewModelScope.launch {
            artRepo.saveSpace(
                spaceModel = spaceModel.copyFrom(artSpace = artSpaceToSave)
            )
        }
    }

    private fun logFailure(result: ArtSpaceResult<Any>) {
        if (result.isFailure)
            Log.d(javaClass.simpleName, result.toString())
    }

    fun updateDrawing(unitPosition: PointF, touchStatus: TouchStatus) =
        viewModelScope.launch {

            if (touchStatus == TouchStatus.STARTED)
                artSpace.recordDrawingHistory()
                    .run { logFailure(this) }

            artSpace.updateDrawing(unitPosition).let { result ->
                when (result) {
                    is ArtSpaceResult.Success -> {
                        _drawing.update { artSpace.state.colorDrawing }
                        artSpace.save()
                    }

                    is ArtSpaceResult.Failure -> logFailure(result)
                }
            }

            if (touchStatus == TouchStatus.ENDED) {
                artSpace.recordDrawingHistory(end = true)
                    .run { logFailure(this) }
                _drawingHistory.update { artSpace.state.drawingHistory }
            }

        }// End updateDrawing

    fun recordPaletteHistory(end: Boolean) =
        viewModelScope.launch {
            artSpace.recordPaletteHistory(end)
            _paletteHistory.update { artSpace.state.paletteHistory }
        }

    fun updateDrawingHistory(redo: Boolean) =
        viewModelScope.launch {
            artSpace.applyDrawingHistory(redo)
            _drawing.update { artSpace.state.colorDrawing }
            _drawingHistory.update { artSpace.state.drawingHistory }
            artSpace.save()
        }

    fun updatePaletteHistory(redo: Boolean) =
        viewModelScope.launch {
            artSpace.applyPaletteHistory(redo = redo)
            _palette.update { artSpace.state.palette.toModel() }
            _drawing.update { artSpace.state.colorDrawing }
            _brushPreview.update { artSpace.state.brushPreview }
            _paletteHistory.update { artSpace.state.paletteHistory }
            artSpace.save()
        }

    fun updatePaletteActiveIndex(paletteIndex: Int) =
        viewModelScope.launch {

            artSpace.updatePaletteActiveIndex(index = paletteIndex)
                .let { result ->
                    when (result) {
                        is ArtSpaceResult.Success -> {
                            _palette.update { artSpace.state.palette.toModel() }
                            _brushPreview.update { artSpace.state.brushPreview }
                        }

                        is ArtSpaceResult.Failure -> Log.d(javaClass.simpleName, result.toString())
                    }
                }

        }

    fun updatePaletteActiveColor(color: Int) =
        viewModelScope.launch {

            artSpace.updatePaletteActiveColor(color = color)
                .let { result ->
                    when (result) {
                        is ArtSpaceResult.Success -> {
                            _palette.update { artSpace.state.palette.toModel() }
                            _drawing.update { artSpace.state.colorDrawing }
                            _brushPreview.update { artSpace.state.brushPreview }
                            artSpace.save()
                        }

                        is ArtSpaceResult.Failure -> Log.d(javaClass.simpleName, result.toString())
                    }
                }

        }

    fun updateBrush(size: Int) =
        viewModelScope.launch {
            artSpace.updateBrushSize(size)
            _brushSize.update { artSpace.state.brushSize }
            _brushPreview.update { artSpace.state.brushPreview }
        }

    fun updateTransformState(transformState: TransformState) =
        viewModelScope.launch {
            _transformState = transformState
        }

    fun updateTransformEnabled(enabled: Boolean) =
        viewModelScope.launch {
            _transformEnabled.update { enabled }
        }

    fun updateHistoryExpanded(expanded: Boolean) =
        viewModelScope.launch {
            _historyExpanded.update { expanded }
        }

}
