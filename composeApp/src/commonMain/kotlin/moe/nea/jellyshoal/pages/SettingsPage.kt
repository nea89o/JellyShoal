package moe.nea.jellyshoal.pages

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import kotlinx.serialization.Serializable
import moe.nea.jellyshoal.views.screens.HomeScreen

@Serializable
object SettingsPage : ShoalRoute {
	@Composable
	override fun Content() {
		Text("TODO")
	}
}

@Serializable
object HomePage : ShoalRoute {
	@Composable
	override fun Content() {
		HomeScreen()
	}
}
