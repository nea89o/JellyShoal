package moe.nea.jellyshoal.views.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import moe.nea.jellyshoal.components.VideoPlayer
import moe.nea.jellyshoal.util.ShoalRoute
import moe.nea.jellyshoal.util.jellyfin.ItemWithProvenance
import org.jellyfin.sdk.api.client.extensions.videosApi


class PlayVideoScreen(
	val item: ItemWithProvenance
) : ShoalRoute {
	@Composable
	override fun Content() {
		// TODO: Add back button on hover
		VideoPlayer(
			item.provenance.createApiClient()
				.videosApi
				.getVideoStreamUrl(
					itemId = item.item.id,
					container = "mkv",
					static = true,
				),
			modifier = Modifier.fillMaxSize()
		)
	}
}
