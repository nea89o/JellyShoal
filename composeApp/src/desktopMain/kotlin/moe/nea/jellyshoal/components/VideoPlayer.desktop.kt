package moe.nea.jellyshoal.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.google.auto.service.AutoService
import io.github.oshai.kotlinlogging.KotlinLogging
import moe.nea.jellyshoal.util.vlc.SkiaBitmapFormatCallback
import moe.nea.jellyshoal.util.vlc.SkiaBitmapRenderCallback
import org.jetbrains.skia.Bitmap
import uk.co.caprica.vlcj.binding.lib.LibVlc
import uk.co.caprica.vlcj.binding.support.init.LinuxNativeInit
import uk.co.caprica.vlcj.binding.support.runtime.RuntimeUtil
import uk.co.caprica.vlcj.factory.discovery.provider.DiscoveryDirectoryProvider
import uk.co.caprica.vlcj.media.Media
import uk.co.caprica.vlcj.media.MediaEventAdapter
import uk.co.caprica.vlcj.media.MediaParsedStatus
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.base.State
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent
import uk.co.caprica.vlcj.player.component.MediaPlayerSpecs

private val logger = KotlinLogging.logger {}

@OptIn(ExperimentalFoundationApi::class)
@Composable
actual fun VideoPlayer(
	url: String,
	modifier: Modifier,
) {
	val bitmap = remember { mutableStateOf<Bitmap?>(null) }
	val mediaPlayerComponent = remember { findMediaPlayerComponent(bitmap) }
	val player = remember { mediaPlayerComponent.mediaPlayer() }
	LaunchedEffect(url) {
		logger.info { "Loading url $url" }
		player.events().addMediaEventListener(object : MediaEventAdapter() {
			override fun mediaStateChanged(media: Media?, newState: State?) {
				logger.info { "Media player state changed to $newState" }
				LibVlc.libvlc_errmsg()?.let { errorMsg ->
					logger.error { "Media player errored: $errorMsg" }
				}
			}

			override fun mediaParsedChanged(
				media: Media?,
				newStatus: MediaParsedStatus?
			) {
				logger.info { "Media player parsed to $newStatus" }
			}
		})
		player.media().start(url)
		// TODO: can vlc4j seek?
	}
	DisposableEffect(Unit) {
		onDispose {
			logger.info { "Deleting player" }
			player.release()
		}
	}
	Box(Modifier.background(Color.Black).fillMaxSize()) {
		Canvas(modifier = Modifier.fillMaxSize()) {
			bitmap.value?.let { bmp ->
				val imageAspectRatio = bmp.width.toFloat() / bmp.height.toFloat()
				val screenAspectRatio = size.width / size.height
				val scaledImageSize: IntSize
				val imageOffset: IntOffset

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
}

@AutoService(DiscoveryDirectoryProvider::class)
class NixDiscoveryProvider : DiscoveryDirectoryProvider {
	override fun priority(): Int {
		return 10
	}

	override fun directories(): Array<out String> {
		return EXTRA_LIB_PATH.split(":").toTypedArray()
	}

	override fun supported(): Boolean {
		return RuntimeUtil.isNix()
	}
}

val EXTRA_LIB_PATH = "/nix/store/p8nhx61w54icjbxgjs15mgk73k95gf75-vlc-3.0.21/lib:"

var lastCall = System.currentTimeMillis()

fun findMediaPlayerComponent(bitmapState: MutableState<Bitmap?>): CallbackMediaPlayerComponent {
	System.setProperty("jna.library.path", System.getProperty("jna.library.path") + ":" + EXTRA_LIB_PATH)
	System.setProperty("VLCJ_INITX", "no")
	LinuxNativeInit.init()
	// TODO: dynamically decide where to load vlc libs from
	logger.info { "Creating player component" }
	return CallbackMediaPlayerComponent(
		MediaPlayerSpecs.callbackMediaPlayerSpec()
			.withBufferFormatCallback(SkiaBitmapFormatCallback)
			.withRenderCallback(SkiaBitmapRenderCallback {
				val lastLastCall = lastCall
				lastCall = System.currentTimeMillis()
				logger.info { "Render frame in ${lastCall - lastLastCall}ms" }
				bitmapState.value = it.setImmutable()
			})
	)
}
