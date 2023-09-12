package com.copixelate.data.intent

import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.net.Uri
import java.lang.ref.WeakReference

object IntentAdapter {

    private lateinit var launchIntent: (Intent) -> Unit

    private lateinit var weakActivity: WeakReference<Activity>

    fun init(activity: Activity) {
        weakActivity = WeakReference(activity)
        launchIntent = { intent ->
            weakActivity.get()?.startActivity(intent)
        }
    }

    fun shareImage(uri: Uri) {
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/png"
            flags = FLAG_GRANT_READ_URI_PERMISSION
            clipData = ClipData.newRawUri("", uri)
        }
        launchIntent(Intent.createChooser(shareIntent, ""))
    }

}
