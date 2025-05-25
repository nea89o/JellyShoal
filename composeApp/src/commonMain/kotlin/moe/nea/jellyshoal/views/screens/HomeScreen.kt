package moe.nea.jellyshoal.views.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import kotlinx.serialization.Serializable
import moe.nea.jellyshoal.util.ShoalRoute

@Serializable
object HomePage : ShoalRoute {

	@Composable
	override fun Content() {
		Text("Home Screen")
	}
}
