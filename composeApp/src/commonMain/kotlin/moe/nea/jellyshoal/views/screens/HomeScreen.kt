package moe.nea.jellyshoal.views.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import moe.nea.jellyshoal.data.findPreference
import moe.nea.jellyshoal.layouts.CenterColumn
import moe.nea.jellyshoal.layouts.DefaultSideBar
import moe.nea.jellyshoal.util.ShoalRoute
import moe.nea.jellyshoal.util.findGlobalNavController

@Serializable
object HomePage : ShoalRoute {

	@Composable
	override fun Content() {
		DefaultSideBar {
			// TODO: read ItemApi#getResumeItems(GetResumeItemsRequest())
			CenterColumn { Text("This is your Home Page! (Which is empty because i can't find the endpoint for fetching the up next section)") }
		}
	}
}
