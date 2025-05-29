package moe.nea.jellyshoal.util.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState



@Composable
fun ProvideWindowState(
	state: WindowState,
	content: @Composable () -> Unit
) {
	CompositionLocalProvider(
		LocalFullScreenHelperProvider provides FullScreenHelperC(state)
	) {
		content()
	}
}

actual class FullScreenHelperC(val state: WindowState) {
	var lastPlacement: WindowPlacement = WindowPlacement.Floating
	actual var isFullScreen: Boolean
		get() = state.placement == WindowPlacement.Fullscreen
		set(value) {
			if (value) {
				val oldPlacement = state.placement
				if (oldPlacement != WindowPlacement.Fullscreen) {
					lastPlacement = oldPlacement
				}
				state.placement = WindowPlacement.Fullscreen
			} else {
				state.placement = lastPlacement
			}
		}
}
