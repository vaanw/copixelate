package com.copixelate.ui.icon

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.copixelate.R

object IconCatalog {

    val draw = Icons.Filled.Draw
    val palette = Icons.Default.Palette
    val paletteAlt = Icons.Outlined.Palette

    val back = Icons.AutoMirrored.Filled.ArrowBack
    val add = Icons.Filled.Add
    val addPhoto = Icons.Outlined.AddPhotoAlternate

    val history = Icons.Default.History
    val undo = Icons.AutoMirrored.Default.Undo
    val redo = Icons.AutoMirrored.Default.Redo

    val collections = Icons.Filled.Collections
    val login = Icons.AutoMirrored.Filled.Login
    val settings = Icons.Filled.Settings
    val contacts = Icons.Filled.Contacts

    val share = Icons.Filled.Share
    val copy = Icons.Filled.ContentCopy
    val moreVertical = Icons.Filled.MoreVert
    val delete = Icons.Outlined.Delete
    val refresh = Icons.Outlined.Refresh
    val save = Icons.Filled.Save

    val left = Icons.AutoMirrored.Filled.KeyboardArrowLeft
    val doubleLeft = Icons.Default.KeyboardDoubleArrowLeft
    val right = Icons.AutoMirrored.Filled.KeyboardArrowRight
    val doubleRight = Icons.Default.KeyboardDoubleArrowRight

    val visible = Icons.Filled.Visibility
    val invisible = Icons.Default.VisibilityOff

    // Custom icons
    @Composable
    fun dragPan() = ImageVector.vectorResource(id = R.drawable.drag_pan)

}
