package moe.nea.jellyshoal.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.onClick
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import com.google.auto.service.AutoService
import io.github.oshai.kotlinlogging.KotlinLogging
import moe.nea.jellyshoal.util.findGlobalNavController
import uk.co.caprica.vlcj.binding.support.init.LinuxNativeInit
import uk.co.caprica.vlcj.binding.support.runtime.RuntimeUtil
import uk.co.caprica.vlcj.factory.discovery.provider.DiscoveryDirectoryProvider
import uk.co.caprica.vlcj.media.Media
import uk.co.caprica.vlcj.media.MediaEventAdapter
import uk.co.caprica.vlcj.media.MediaParsedStatus
import uk.co.caprica.vlcj.player.base.State
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent

val logger = KotlinLogging.logger {}

@OptIn(ExperimentalFoundationApi::class)
@Composable
actual fun VideoPlayer(
	url: String,
	modifier: Modifier,
) {
	val mediaPlayerComponent = remember { findMediaPlayerComponent() }
	val player = remember { mediaPlayerComponent.mediaPlayer() }
	val factory = remember { { mediaPlayerComponent.videoSurfaceComponent() } }
	LaunchedEffect(url) {
		logger.info { "Loading url $url" }
		player.media().play(url)
		player.media().events().addMediaEventListener(object : MediaEventAdapter() {
			override fun mediaStateChanged(media: Media?, newState: State?) {
				logger.info { "Media player state changed to $newState" }
			}

			override fun mediaParsedChanged(
				media: Media?,
				newStatus: MediaParsedStatus?
			) {
				logger.info { "Media player parsed to $newStatus" }
			}
		})
		// TODO: can vlc4j seek?
	}
	DisposableEffect(Unit) {
		onDispose {
			logger.info { "Deleting player" }
			player.release()
		}
	}
	Column(Modifier.background(Color.Black).fillMaxSize()) {
		// TODO: figure out why this one pixel needs to be here
		Column {
			val nav = findGlobalNavController()
			Text(
				"Go Back",
				color = Color.White,
				modifier = Modifier.onClick(
					enabled = true,
					onClick = {
//						player.controls().setTime(1.minutes.inWholeMilliseconds)
						nav.goBack()
					})
			)
			SwingPanel(
				factory = factory,
				background = Color.Blue,
				modifier = modifier.fillMaxSize(),
				update = {}
			)
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

fun findMediaPlayerComponent(): CallbackMediaPlayerComponent {
	System.setProperty("jna.library.path", System.getProperty("jna.library.path") + ":" + EXTRA_LIB_PATH)
	System.setProperty("VLCJ_INITX", "no")
	LinuxNativeInit.init()
	// TODO: dynamically decide where to load vlc libs from
	logger.info { "Created player component" }
	return CallbackMediaPlayerComponent()
}
