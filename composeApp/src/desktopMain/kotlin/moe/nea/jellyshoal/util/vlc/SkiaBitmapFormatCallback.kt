package moe.nea.jellyshoal.util.vlc

import io.github.oshai.kotlinlogging.KotlinLogging
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormat
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormatCallback
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.format.RV32BufferFormat
import java.nio.ByteBuffer

/**
 * Format callback for [SkiaBitmapRenderCallback]
 */
object SkiaBitmapFormatCallback : BufferFormatCallback {
	private val logger = KotlinLogging.logger { }
	override fun getBufferFormat(
		sourceWidth: Int,
		sourceHeight: Int
	): BufferFormat? {
		logger.info { "Obtaining format for $sourceWidth x $sourceHeight" }
		return RV32BufferFormat(sourceWidth, sourceHeight)
	}

	override fun newFormatSize(
		bufferWidth: Int,
		bufferHeight: Int,
		displayWidth: Int,
		displayHeight: Int
	) {
		logger.info { "New format size: $displayWidth x $displayHeight" }
	}

	override fun allocatedBuffers(buffers: Array<out ByteBuffer>) {
		logger.info { "New buffers allocated (${buffers.size})" }
	}
}
