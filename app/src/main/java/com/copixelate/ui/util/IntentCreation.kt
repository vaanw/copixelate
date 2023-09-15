package com.copixelate.ui.util

import android.content.ClipData
import android.content.Intent
import android.net.Uri

fun createShareImageIntent(uri: Uri): Intent =
    createChooser(
        Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/png"
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            clipData = ClipData.newRawUri("", uri)
        }
    )

private fun createChooser(target: Intent) = Intent.createChooser(target, "")
