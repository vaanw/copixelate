package com.copixelate.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.copixelate.art.ArtSpace
import com.copixelate.art.ArtSpaceResult
import com.copixelate.art.PointF
import com.copixelate.data.model.SpaceModel
import com.copixelate.data.repo.ArtRepo
import com.copixelate.data.repo.UiRepo
import com.copixelate.data.repo.toArtSpace
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ArtViewModel : ViewModel() {

    private val artRepo = ArtRepo
    private val uiRepo = UiRepo

    private var artSpace = ArtSpace()

    private val _drawing = MutableStateFlow(artSpace.state.colorDrawing)
    private val _palette = MutableStateFlow(artSpace.state.palette)
    private val _activeColor = MutableStateFlow(artSpace.state.activeColor)
    private val _brushPreview = MutableStateFlow(artSpace.state.brushPreview)
    private val _brushSize = MutableStateFlow(artSpace.state.brushSize)

    val drawing = _drawing.asStateFlow()
    val palette = _palette.asStateFlow()
    val activeColor = _activeColor.asStateFlow()
    val brushPreview = _brushPreview.asStateFlow()
    val brushSize = _brushSize.asStateFlow()

    init {
        viewModelScope.launch {
            @OptIn(ExperimentalCoroutinesApi::class)
            uiRepo.currentSpaceIdFlow()
                .flatMapLatest { currentId ->
                    // Find SpaceModel using currentId
                    artRepo.spaceByIdFlow(id = currentId)
                        .map { model: SpaceModel? ->
                            model?.toArtSpace()
                            // if SpaceModel is not found, find a default
                                ?: artRepo.getDefaultSpace()?.also { spaceModel ->
                                    // localId should never be null since it's provided by Room
                                    uiRepo.saveCurrentSpaceId(spaceId = spaceModel.id.localId!!)
                                }?.toArtSpace()
                                // if a default SpaceModel is not found, create a new one
                                ?: artSpace.also { value ->
                                    val newId = artRepo.saveSpace(artSpace = value)[0]
                                    uiRepo.saveCurrentSpaceId(spaceId = newId)
                                }
                        }
                }.collect { newArtSpace ->
                    refreshArtSpace(newArtSpace = newArtSpace)
                }

        } // End viewModelScope.launch()
    } // End init()

    private fun refreshArtSpace(newArtSpace: ArtSpace) {
        artSpace = newArtSpace
        _drawing.value = artSpace.state.colorDrawing
        _palette.value = artSpace.state.palette
        _activeColor.value = artSpace.state.activeColor
        _brushPreview.value = artSpace.state.brushPreview
    }

    fun updateDrawing(unitPosition: PointF) =
        viewModelScope.launch {

            artSpace.updateDrawing(unitPosition).let { result ->
                when (result) {
                    is ArtSpaceResult.Success -> _drawing.value = artSpace.state.colorDrawing
                    is ArtSpaceResult.Failure -> Log.d(javaClass.simpleName, result.toString())
                }
            }

        }

    fun updatePalette(unitPosition: PointF) =
        viewModelScope.launch {

            artSpace.updatePalette(unitPosition).let { result ->
                when (result) {
                    is ArtSpaceResult.Success -> {
                        _palette.value = artSpace.state.palette
                        _activeColor.value = artSpace.state.activeColor
                        _brushPreview.value = artSpace.state.brushPreview
                    }
                    is ArtSpaceResult.Failure -> Log.d(javaClass.simpleName, result.toString())
                }
            }

        }

    fun updateBrush(size: Int) =
        viewModelScope.launch {
            artSpace.updateBrushSize(size)
            _brushSize.value = artSpace.state.brushSize
            _brushPreview.value = artSpace.state.brushPreview
        }

}
