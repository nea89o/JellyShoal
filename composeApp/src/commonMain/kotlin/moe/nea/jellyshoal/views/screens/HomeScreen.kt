package moe.nea.jellyshoal.views.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import kotlinx.serialization.Serializable
import moe.nea.jellyshoal.data.findPreference
import moe.nea.jellyshoal.util.ShoalRoute
import moe.nea.jellyshoal.util.jellyfin.sharedJellyfinInstance

@Serializable
object HomePage : ShoalRoute {

	@Composable
	override fun Content() {
		val (accounts) = findPreference { accounts }
		val clients = accounts.map {

		}
		Column {
			Text("Here is a list of all your accounts:", fontSize = 30.sp)
			accounts.map { account ->
				Text("${account.token} on ${account.server}")
			}
		}
	}
}
