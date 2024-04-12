package com.copixelate.data.storage

import android.app.Application
import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object StorageAdapter {

    private lateinit var contentResolver: ContentResolver
    private lateinit var cacheDir: File
    private lateinit var getShareableUri: (File) -> Uri

    fun init(application: Application) {
        contentResolver = application.contentResolver
        cacheDir = application.cacheDir
        getShareableUri = { file ->
            FileProvider.getUriForFile(application, "com.copixelate.fileprovider", file)
        }
    }

    fun writeNewImageFile(bitmap: Bitmap, fileName: String): Uri? {

        // Find external storage location
        val imageCollection = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ->
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            else ->
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val newImageDetails = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$fileName.png")
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
                return uri
            }

        return null

    }

    fun createTemporaryShareableImage(bitmap: Bitmap): Uri {

        val imageDir: File = File(cacheDir, "/images/").apply {
            when (exists()) {
                false -> mkdir()
                true -> {
                    deleteRecursively()
                    mkdir()
                }
            }
        }

        val newFile = File(imageDir, "/temporary-shared.png")

        FileOutputStream(newFile).use { fos ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        }

        return getShareableUri(newFile)
    }

}
