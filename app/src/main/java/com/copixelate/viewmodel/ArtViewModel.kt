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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ArtViewModel : ViewModel() {

    private val artRepo = ArtRepo
    private val uiRepo = UiRepo

    private var artSpace = ArtSpace()
    private var spaceModel = artSpace.toModel()

    private val _drawing = MutableStateFlow(artSpace.state.colorDrawing)
    private val _palette = MutableStateFlow(artSpace.state.palette)
    private val _brushPreview = MutableStateFlow(artSpace.state.brushPreview)
    private val _brushSize = MutableStateFlow(artSpace.state.brushSize)

    val drawing = _drawing.asStateFlow()
    val palette = _palette.asStateFlow()
    val brushPreview = _brushPreview.asStateFlow()
    val brushSize = _brushSize.asStateFlow()

    private val _contentReady = MutableStateFlow(false)
    val contentReady = _contentReady.asStateFlow()

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
                                // localId should never be null since it's provided by Room
                                uiRepo.saveCurrentSpaceId(spaceId = value.id.localId!!)
                            }
                        // if a default SpaceModel is not found, create a new one
                        ?: run {
                            val newId = artRepo.saveSpace(artSpace = artSpace.createDefaultArt())
                            uiRepo.saveCurrentSpaceId(spaceId = newId)
                        }

                } // End collect()
        } // End viewModelScope.launch()
    } // End init()

    private fun refreshArtSpace(newArtSpace: ArtSpace) {
        artSpace = newArtSpace
        _drawing.update { artSpace.state.colorDrawing }
        _palette.update { artSpace.state.palette }
        _brushPreview.update { artSpace.state.brushPreview }
    }

    fun updateDrawing(unitPosition: PointF) =
        viewModelScope.launch {

            artSpace.updateDrawing(unitPosition).let { result ->
                when (result) {
                    is ArtSpaceResult.Success -> {
                        _drawing.update { artSpace.state.colorDrawing }
                        artRepo.saveSpace(
                            spaceModel = spaceModel.copyFrom(artSpace = artSpace)
                        )
                    }
                    is ArtSpaceResult.Failure -> Log.d(javaClass.simpleName, result.toString())
                }
            }

        }

    fun updatePaletteActiveIndex(paletteIndex: Int) =
        viewModelScope.launch {

            artSpace.updatePaletteActiveIndex(paletteIndex = paletteIndex)
                .let { result ->
                    when (result) {
                        is ArtSpaceResult.Success -> {
                            _palette.update { artSpace.state.palette }
                            _brushPreview.update { artSpace.state.brushPreview }
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

}
