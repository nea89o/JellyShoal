package moe.nea.jellyshoal.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.google.auto.service.AutoService
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.delay
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
import uk.co.caprica.vlcj.player.base.State
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent
import uk.co.caprica.vlcj.player.component.MediaPlayerSpecs

private val logger = KotlinLogging.logger {}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun VideoPlayer(
	url: String,
	modifier: Modifier,
) {
	val bitmap = remember { mutableStateOf<Bitmap?>(null) }
	val mediaPlayerComponent = remember { findMediaPlayerComponent(bitmap) }
	val player = remember { mediaPlayerComponent.mediaPlayer() }
	var lastActivityGeneration by remember { mutableStateOf(0L) }
	var isOverlayVisible by remember { mutableStateOf(true) }
	LaunchedEffect(lastActivityGeneration) {
		delay(2000)
		isOverlayVisible = false
	}

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
	Box(
		modifier.background(Color.Black)
			.fillMaxSize()
			.onPointerEvent(PointerEventType.Move) {
				isOverlayVisible = true
				lastActivityGeneration = System.currentTimeMillis()
			}
	) {
		Canvas(modifier = Modifier.fillMaxSize()) {
			bitmap.value?.let { bmp ->
				val imageAspectRatio = bmp.width.toFloat() / bmp.height.toFloat()
				val screenAspectRatio = size.width / size.height
				val scaledImageSize: IntSize
				val imageOffset: IntOffset
				logger.trace {
					val lastLastCall = lastCall
					lastCall = System.currentTimeMillis()
					"Received new frame after ${lastCall - lastLastCall}ms"
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
		AnimatedVisibility(
			isOverlayVisible,
			modifier = Modifier.fillMaxSize(),
			enter = fadeIn(animationSpec = tween(durationMillis = 200)),
			exit = fadeOut(animationSpec = tween(durationMillis = 600)),
		) {
			Column(
				verticalArrangement = Arrangement.SpaceBetween,
				modifier = Modifier.matchParentSize(),
			) {
				Row { // Top Row
					Text("This is on top", color = Color.White)
				}
				Row { // Bottom Row
					Text("This is on bottom", color = Color.White)
				}
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
				bitmapState.value = it.setImmutable()
			})
	)
}
