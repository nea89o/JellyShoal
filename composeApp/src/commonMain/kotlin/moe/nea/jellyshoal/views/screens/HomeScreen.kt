package moe.nea.jellyshoal.views.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import moe.nea.jellyshoal.build.BuildConfig
import moe.nea.jellyshoal.components.MovieCard
import moe.nea.jellyshoal.data.Account
import moe.nea.jellyshoal.data.findPreference
import moe.nea.jellyshoal.layouts.DefaultSideBar
import moe.nea.jellyshoal.util.ShoalRoute
import moe.nea.jellyshoal.util.jellyfin.ItemWithProvenance
import moe.nea.jellyshoal.util.jellyfin.withProvenance
import org.jellyfin.sdk.api.operations.ItemsApi

@Serializable
object HomePage : ShoalRoute {

	@Composable
	override fun Content() {
		val accounts by findPreference { accounts }
		val resumeItems = remember { mutableStateMapOf<Account, List<ItemWithProvenance>>() }
		accounts.map { account ->
			LaunchedEffect(account) {
				val api = ItemsApi(account.createApiClient())
				val results = api.getResumeItems(userId = null)
					.content
					.items
					.map { it.withProvenance(account) }
				resumeItems.put(account, results)
			}
		}

		DefaultSideBar {
			Column(modifier = Modifier.fillMaxSize()) {
				Text(
					"Welcome to ${BuildConfig.BRAND}", style = MaterialTheme.typography.headlineLarge,
					modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
				)
				LazyColumn {
					items(accounts) { account ->
						Column(modifier = Modifier.fillMaxWidth()) {
							Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
								Icon(Icons.Outlined.ChevronRight, contentDescription = null)
								Text(account.userFriendlyName(), style = MaterialTheme.typography.headlineSmall)
							}
							val items = resumeItems[account]
							if (items == null) {
								Row(modifier = Modifier.align(Alignment.CenterHorizontally).padding(30.dp)) {
									RotatingLoadingIndicator()
									Text("Loading")
								}
							} else {
								LazyRow(modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp)) {
									items(items) { resume ->
										MovieCard(resume, modifier = Modifier.width(740.dp))
									}
								}
							}
						}
					}
				}
			}
			// TODO: read ItemApi#getResumeItems(GetResumeItemsRequest())
		}
	}
}

@Composable
fun RotatingLoadingIndicator() {
	val infiniteTransition = rememberInfiniteTransition()
	val rotation by infiniteTransition.animateFloat(
		0F,
		360F,
		animationSpec = infiniteRepeatable(
			animation = tween(
				durationMillis = 2500,
				easing = LinearEasing
			),
			repeatMode = RepeatMode.Restart
		)
	)
	Icon(
		Icons.Outlined.Sync,
		contentDescription = null,
		modifier = Modifier.rotate(-rotation)
	)

}
