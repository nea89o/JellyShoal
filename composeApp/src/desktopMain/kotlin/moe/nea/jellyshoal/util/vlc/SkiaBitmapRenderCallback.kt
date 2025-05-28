package moe.nea.jellyshoal.util.vlc

import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.ImageInfo
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.embedded.videosurface.CallbackVideoSurface
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormat
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.RenderCallback
import java.nio.ByteBuffer

/**
 * Receive images from VLC to be rendered to a [skia bitmap][Bitmap]
 *
 * @see CallbackVideoSurface
 * @see SkiaBitmapFormatCallback
 */
class SkiaBitmapRenderCallback(val bitmapConsumer: (Bitmap) -> Unit) : RenderCallback {
	companion object {
		private val logger = KotlinLogging.logger { }
	}

	override fun lock(mediaPlayer: MediaPlayer?) {
		logger.trace { "Locked onto media player" }
	}

	override fun display(
		mediaPlayer: MediaPlayer,
		nativeBuffers: Array<out ByteBuffer>,
		bufferFormat: BufferFormat,
		displayWidth: Int,
		displayHeight: Int
	) {
		logger.trace { "Displaying $displayWidth x $displayHeight in $bufferFormat" }
		val sourceBuffer = nativeBuffers[0]
		val pixelWidth = 4
		val bufferSize = displayWidth * displayHeight * pixelWidth
		sourceBuffer.position(0)
		require(bufferSize == sourceBuffer.remaining()) {
			"Invalid buffer size ${sourceBuffer.remaining()}"
		}
		val byteBuffer = ByteArray(bufferSize)
		sourceBuffer.get(byteBuffer, 0, bufferSize)
		val bitmap = Bitmap()
		bitmap.installPixels(
			ImageInfo(displayWidth, displayHeight, ColorType.BGRA_8888, ColorAlphaType.PREMUL),
			byteBuffer,
			displayWidth * pixelWidth
		)

		bitmapConsumer(bitmap)
	}

	override fun unlock(mediaPlayer: MediaPlayer?) {
		logger.trace { "Unlocked from media player" }
	}
}
