package moe.nea.jellyshoal

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.navigation.compose.rememberNavController

fun main() = application {
	val globalNavHost = rememberNavController()
	Window(
		onCloseRequest = ::exitApplication,
		title = "JellyShoal",
//		onKeyEvent = {
//			println("Key handled: ${it.key} (${it.nativeKeyEvent})")
//			when (it.key) {
//				Key.NavigatePrevious -> {
//					globalNavHost.popBackStack()
//					true
//				}
//
//				else -> false
//			}
//		}
	) {
		App(globalNavHost)
	}
}
