package com.copixelate.ui.common

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.copixelate.ui.icon.IconCatalog

@Composable
fun AddItemFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Icon(
            imageVector = IconCatalog.add,
            contentDescription = "Localized description"
        )
    }
}
