package moe.nea.jellyshoal.pages

import androidx.compose.runtime.Composable
import kotlinx.serialization.Serializable
import moe.nea.jellyshoal.views.screens.AddServerScreen

@Serializable
data class AddServerPage(
	val serverUrl: String,
) : ShoalRoute {
	@Composable
	override fun Content() {
		AddServerScreen(serverUrl)
	}
}
