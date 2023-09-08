package com.copixelate.data.media

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileOutputStream

object MediaStoreAdapter {

    private lateinit var contentResolver: ContentResolver

    fun init(contentResolver: ContentResolver) {
        this.contentResolver = contentResolver
    }

    suspend fun writeNewImageFile(bitmap: Bitmap, fileName: String) {
        withContext(Dispatchers.IO) {
            writeNewImageFileSynchronous(bitmap, fileName)
        }
    }

    private fun writeNewImageFileSynchronous(bitmap: Bitmap, fileName: String) {

        // Find external storage location
        val imageCollection = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ->
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            else ->
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val newImageDetails = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        contentResolver
            // Write new "pending" file
            .insert(imageCollection, newImageDetails)
            ?.let { uri ->
                // "w" for write.
                contentResolver.openFileDescriptor(uri, "w", null).use { pfd ->
                    // Write data into the file
                    FileOutputStream(pfd?.fileDescriptor).use { fos ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                    }
                }
                // Release the "pending" status
                newImageDetails.clear()
                newImageDetails.put(MediaStore.Images.Media.IS_PENDING, 0)
                contentResolver.update(uri, newImageDetails, null, null)

            }

    }// End writeNewImageFileSynchronous

}
