package moe.nea.jellyshoal.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.skia.Bitmap

private val logger = KotlinLogging.logger {}
private var lastFrameTime = System.currentTimeMillis()

@Composable
fun VideoCanvas(bitmap: androidx.compose.runtime.State<Bitmap?>) {
	Canvas(modifier = Modifier.fillMaxSize()) {
		bitmap.value?.let { bmp ->
			val imageAspectRatio = bmp.width.toFloat() / bmp.height.toFloat()
			val screenAspectRatio = size.width / size.height
			val scaledImageSize: IntSize
			val imageOffset: IntOffset
			logger.trace {
				val lastLastCall = lastFrameTime
				lastFrameTime = System.currentTimeMillis()
				"Received new frame after ${lastFrameTime - lastLastCall}ms"
			}

			if (imageAspectRatio > screenAspectRatio) {
				// The image is wider than the screen
				scaledImageSize =
					IntSize(
						size.width.toInt(),
						(size.width / imageAspectRatio).toInt()
					)
				imageOffset = IntOffset(
					0,
					((size.height - scaledImageSize.height) / 2).toInt()
				)
			} else {
				// The image is higher 🌿 than the screen
				scaledImageSize =
					IntSize(
						(size.height * imageAspectRatio).toInt(),
						size.height.toInt(),
					)
				imageOffset = IntOffset(
					((size.width - scaledImageSize.width) / 2).toInt(),
					0
				)

			}
			drawImage(
				bmp.asComposeImageBitmap(),
				dstOffset = imageOffset,
				dstSize = scaledImageSize,
			)
		}

	}
}
