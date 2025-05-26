package moe.nea.jellyshoal.layouts

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import moe.nea.jellyshoal.util.ShoalRoute
import moe.nea.jellyshoal.util.findGlobalNavController
import moe.nea.jellyshoal.views.screens.HomePage
import moe.nea.jellyshoal.views.screens.SearchPage

@Composable
fun DefaultSideBar(
	content: @Composable () -> Unit
) {
	val nav = findGlobalNavController()
	val currentPage = nav.currentScreen

	@Composable
	fun itemFor(page: ShoalRoute, icon: ImageVector, label: String) {
		NavigationDrawerItem(
			icon = { Icon(icon, contentDescription = null) },
			label = { Text(label) },
			selected = page == currentPage,
			onClick = {
				nav.navigate(page)
			},
			modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
		)
	}

	PermanentNavigationDrawer(
		drawerContent = {
			PermanentDrawerSheet(Modifier.width(250.dp)) {
//					Text(BuildConfig.BRAND, modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold, fontSize = 30.dp)
				itemFor(HomePage, Icons.Outlined.Home, "Home")
				itemFor(SearchPage, Icons.Outlined.Search, "Search")

			}
		},
		content = content
	)
}

