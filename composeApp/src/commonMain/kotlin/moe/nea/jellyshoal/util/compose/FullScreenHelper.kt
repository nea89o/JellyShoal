package moe.nea.jellyshoal.util.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

expect class FullScreenHelperC {
	var isFullScreen: Boolean
}

fun FullScreenHelperC.toggleFullScreen() {
	isFullScreen = !isFullScreen
}


val LocalFullScreenHelperProvider = staticCompositionLocalOf<FullScreenHelperC> { error("Not provided") }

@Composable
fun findFullScreenHelper(): FullScreenHelperC {
	return LocalFullScreenHelperProvider.current
}

