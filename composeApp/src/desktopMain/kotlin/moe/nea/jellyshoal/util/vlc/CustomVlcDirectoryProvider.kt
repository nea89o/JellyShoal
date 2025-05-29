package moe.nea.jellyshoal.util.vlc

import androidx.compose.runtime.MutableState
import com.google.auto.service.AutoService
import io.github.oshai.kotlinlogging.KotlinLogging
import moe.nea.jellyshoal.data.DataStore
import moe.nea.jellyshoal.data.Preferences
import moe.nea.jellyshoal.util.joinLibraryPath
import org.jetbrains.skia.Bitmap
import uk.co.caprica.vlcj.binding.support.init.LinuxNativeInit
import uk.co.caprica.vlcj.binding.support.runtime.RuntimeUtil
import uk.co.caprica.vlcj.factory.discovery.provider.DiscoveryDirectoryProvider
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent
import uk.co.caprica.vlcj.player.component.MediaPlayerSpecs

private val logger = KotlinLogging.logger { }

@AutoService(DiscoveryDirectoryProvider::class)
class CustomVlcDirectoryProvider : DiscoveryDirectoryProvider {
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

val EXTRA_LIB_PATH
	get() =
		Preferences(DataStore)
			.externalVlcPath
			.getCurrent()

fun findMediaPlayerComponent(bitmapState: MutableState<Bitmap?>): CallbackMediaPlayerComponent {
	System.setProperty("jna.library.path", joinLibraryPath(System.getProperty("jna.library.path"), EXTRA_LIB_PATH))
	System.setProperty("VLCJ_INITX", "no")
	if (RuntimeUtil.isNix()) {
		LinuxNativeInit.init()
	}
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
