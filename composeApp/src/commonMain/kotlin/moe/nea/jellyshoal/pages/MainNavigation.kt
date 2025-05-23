package moe.nea.jellyshoal.pages

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import moe.nea.jellyshoal.views.screens.AddServerScreen
import moe.nea.jellyshoal.views.screens.HomeScreen
import moe.nea.jellyshoal.views.screens.WelcomeView
import kotlin.reflect.KClass

sealed interface ShoalRoute {}

val globalNavigationLocal =
	staticCompositionLocalOf<TypedNavHostController<ShoalRoute>> { error("Global Navigation Scope not provided") }

class TypedNavHostController<T : Any>(
	val controller: NavHostController,
) {
	fun navigate(page: T) {
		println("Navigate before: ${controller.currentBackStack.value}")
		controller.navigate(page)
		println("Navigate after: ${controller.currentBackStack.value}")
	}

}

@Composable
fun findGlobalNavController(): TypedNavHostController<ShoalRoute> {
	// TODO: wrap into a sealed class of some sort for routes
	return globalNavigationLocal.current
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NavigationContext(navHostController: NavHostController?) {
	val globalNavController = navHostController ?: rememberNavController()

	CompositionLocalProvider(globalNavigationLocal provides TypedNavHostController<ShoalRoute>(globalNavController)) {
		NavHost(
			navController = globalNavController,
			startDestination = WelcomePage,
			modifier = Modifier.fillMaxSize().pointerInput(Unit) {
				println("Handling pointer events")
				while (true) {
					val event = awaitPointerEventScope { awaitPointerEvent() }
					if (event.type != PointerEventType.Press) continue
					if (event.button.isBackButtonFixed && event.type == PointerEventType.Press) {
						println("Attempting back navigation with stack ${globalNavController.currentBackStack.value}")
						globalNavController.popBackStack()
					}
				}
			}
		) {
			route<WelcomePage> { WelcomeView() }
			route<AddServerPage> { AddServerScreen(it.route.serverUrl) }
			route<SettingsPage> { Text("TODO") }
			route<HomePage> { HomeScreen() }
		}
	}
}

val PointerButton?.isBackButtonFixed
	get() = this?.index == 5 || this?.index == 3
val PointerButton?.isForwardButtonFixed
	get() = this?.index == 6 || this?.index == 4


data class TypedNavBackStackEntry<T : Any>(val entry: NavBackStackEntry, val typ: KClass<T>) {
	val route get() = entry.toRoute<T>(typ)
}

inline fun <reified T : ShoalRoute> NavGraphBuilder.route(crossinline content: @Composable AnimatedContentScope.(TypedNavBackStackEntry<T>) -> Unit) {
	composable<T> {
		content(TypedNavBackStackEntry(it, T::class))
	}
}


