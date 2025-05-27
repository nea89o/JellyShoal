package moe.nea.jellyshoal.layouts

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import moe.nea.jellyshoal.util.ShoalRoute
import moe.nea.jellyshoal.util.findGlobalNavController
import moe.nea.jellyshoal.views.screens.HomePage
import moe.nea.jellyshoal.views.screens.SearchPage
import moe.nea.jellyshoal.views.screens.SettingsPage

@Composable
fun DefaultSideBar(
	content: @Composable () -> Unit
) {
	val nav = findGlobalNavController()
	val currentPage = nav.currentScreen

	@Composable
	fun itemFor(
		page: ShoalRoute, icon: ImageVector, label: String,
		modifier: Modifier = Modifier
	) {
		NavigationDrawerItem(
			icon = { Icon(icon, contentDescription = null) },
			label = { Text(label) },
			selected = page == currentPage,
			onClick = {
				nav.navigate(page)
			},
			modifier = modifier.padding(horizontal = 12.dp, vertical = 5.dp)
		)
	}

	PermanentNavigationDrawer(
		drawerContent = {
			PermanentDrawerSheet(Modifier.width(250.dp)) {
//					Text(BuildConfig.BRAND, modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold, fontSize = 30.dp)
				Column(
					verticalArrangement = Arrangement.SpaceBetween,
					modifier = Modifier.fillMaxHeight()
						.padding(vertical = 8.dp)
				) {
					Column {
						itemFor(HomePage, Icons.Outlined.Home, "Home")
						itemFor(SearchPage, Icons.Outlined.Search, "Search")
					}
					Column {
						itemFor(SettingsPage, Icons.Outlined.Settings, "Settings")
					}
				}
			}
		},
		content = {
			// TODO: janky hack, mate (this is not what scaffold is for, really, we should be defining the colours another way)
			Scaffold(
				content = { paddingValues ->
					Column(modifier = Modifier.padding(paddingValues)) {
						content()
					}
				}
			)
		}
	)
}

