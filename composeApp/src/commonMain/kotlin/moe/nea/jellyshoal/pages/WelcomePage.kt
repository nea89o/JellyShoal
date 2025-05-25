package moe.nea.jellyshoal.pages

import androidx.compose.runtime.Composable
import kotlinx.serialization.Serializable
import moe.nea.jellyshoal.views.screens.WelcomeView

@Serializable
object WelcomePage : ShoalRoute {
	@Composable
	override fun Content() {
		WelcomeView()
	}
}
