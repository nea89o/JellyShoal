package moe.nea.jellyshoal

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import moe.nea.jellyshoal.build.BuildConfig

fun main() = application {
	Window(
		onCloseRequest = ::exitApplication,
		title = BuildConfig.BRAND,
	) {
		App()
	}
}
