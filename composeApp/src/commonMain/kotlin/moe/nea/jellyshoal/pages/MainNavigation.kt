package moe.nea.jellyshoal.pages

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import moe.nea.jellyshoal.views.screens.AddServerScreen
import moe.nea.jellyshoal.views.screens.WelcomeView

val globalNavigationLocal =
	staticCompositionLocalOf<NavHostController> { error("Global Navigation Scope not provided") }

@Composable
fun findGlobalNavController(): NavHostController {
	return globalNavigationLocal.current
}

@Composable
fun NavigationContext() {
	val globalNavController = rememberNavController()

	CompositionLocalProvider(globalNavigationLocal provides globalNavController) {
		NavHost(
			navController = globalNavController,
			startDestination = WelcomePage,
		) {
			composable<WelcomePage> { WelcomeView() }
			composable<AddServerPage> {
				AddServerScreen(it.toRoute<AddServerPage>().serverUrl)
			}
			composable<SettingsPage> { Text("TODO") }
		}
	}

}

