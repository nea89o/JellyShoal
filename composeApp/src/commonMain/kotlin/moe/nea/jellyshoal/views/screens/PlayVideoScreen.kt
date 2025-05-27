package moe.nea.jellyshoal.views.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import moe.nea.jellyshoal.components.VideoPlayer
import moe.nea.jellyshoal.util.ShoalRoute


class PlayVideoScreen(
	val url: String // TODO: load the actual item to retrieve get next item and update play percentage
) : ShoalRoute {
	@Composable
	override fun Content() {
		// TODO: Add back button on hover
		VideoPlayer(url, modifier = Modifier.fillMaxSize())
	}
}
