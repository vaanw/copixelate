package com.copixelate.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.copixelate.data.ArtRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.copixelate.art.ArtSpace
import com.copixelate.art.ArtSpaceResult
import com.copixelate.art.PointF

class ArtViewModel(application: Application) : AndroidViewModel(application) {

    private val artSpace = ArtSpace()

    private val repo = ArtRepo(application.applicationContext)

    init {
        viewModelScope.launch {
//            repo.saveSpace()
//            artSpace.state.drawing
        }
    }

    private val _drawing = MutableStateFlow(artSpace.state.drawing)
    private val _palette = MutableStateFlow(artSpace.state.palette)
    private val _activeColor = MutableStateFlow(artSpace.state.activeColor)
    private val _brushPreview = MutableStateFlow(artSpace.state.brushPreview)

    val drawing = _drawing.asStateFlow()
    val palette = _palette.asStateFlow()
    val activeColor = _activeColor.asStateFlow()
    val brushPreview = _brushPreview.asStateFlow()

    val brushSize = artSpace.state.brushSize

    fun updateDrawing(unitPosition: PointF) =
        viewModelScope.launch {

            artSpace.updateDrawing(unitPosition).let { result ->
                when (result) {
                    is ArtSpaceResult.Success -> _drawing.value = artSpace.state.drawing
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
            _brushPreview.value = artSpace.state.brushPreview

        }

}
