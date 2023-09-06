package com.copixelate.ui.common

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.copixelate.art.PixelGrid


/**
 * A composable that lays out and draws an [IntArray] of sRGB colors without filtering
 *
 * @param colors IntArray of sRGB colors used to initialize the pixels. This array must be at least
 * as large as width * height
 * @param width Int width of the bitmap
 * @param height Int height of the bitmap
 * @param contentScale scale parameter used to determine the aspect ratio scaling to be used if the
 * bounds are a different size from the intrinsic size of the [ImageBitmap]
 * @param contentDescription text used by accessibility services to describe this image
 * @param modifier Modifier used to adjust the layout algorithm or draw decoration content (ex.
 * background)
 */
@Composable
internal fun BitmapImage(
    colors: IntArray,
    width: Int,
    height: Int,
    contentScale: ContentScale,
    contentDescription: String,
    modifier: Modifier = Modifier
) {

    val bitmapConfig = Bitmap.Config.ARGB_8888
    val bitmap = Bitmap.createBitmap(colors, width, height, bitmapConfig)

    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = contentDescription,
        filterQuality = FilterQuality.None,
        modifier = modifier,
        contentScale = contentScale
    )
}

/**
 * A composable that lays out and draws a given 1x1 color without filtering
 *
 * @param color Int sRGB color
 * @param contentScale scale parameter used to determine the aspect ratio scaling to be used if the
 * bounds are a different size from the intrinsic size of the [ImageBitmap]
 * @param contentDescription text used by accessibility services to describe what this image
 * @param modifier Modifier used to adjust the layout algorithm or draw decoration content (ex.
 * background)
 */
@Composable
internal fun BitmapImage(
    color: Int,
    contentScale: ContentScale,
    contentDescription: String,
    modifier: Modifier = Modifier
) = BitmapImage(
    colors = intArrayOf(color),
    width = 1,
    height = 1,
    contentScale, contentDescription, modifier
)

/**
 * A composable that lays out and draws a given [PixelGrid] without filtering
 *
 * @param pixelGrid The [PixelGrid] to draw unfiltered
 * @param contentScale scale parameter used to determine the aspect ratio scaling to be used if the
 * bounds are a different size from the intrinsic size of the [ImageBitmap]
 * @param contentDescription text used by accessibility services to describe what this image
 * @param modifier Modifier used to adjust the layout algorithm or draw decoration content (ex.
 * background)
 */
@Composable
internal fun BitmapImage(
    pixelGrid: PixelGrid,
    contentDescription: String,
    contentScale: ContentScale,
    modifier: Modifier = Modifier
) = BitmapImage(
    colors = pixelGrid.pixels,
    width = pixelGrid.size.x,
    height = pixelGrid.size.y,
    contentScale, contentDescription, modifier
)
