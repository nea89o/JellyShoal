package moe.nea.jellyshoal

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import moe.nea.jellyshoal.build.BuildConfig
import moe.nea.jellyshoal.util.compose.ProvideWindowState

fun main() = application {
	val windowState = rememberWindowState()
	Window(
		onCloseRequest = ::exitApplication,
		title = BuildConfig.BRAND,
		state = windowState,
	) {
		ProvideWindowState(windowState) {
			App()
		}
	}
}
