package moe.nea.jellyshoal.util

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import io.github.oshai.kotlinlogging.KotlinLogging
import moe.nea.jellyshoal.data.findPreference
import moe.nea.jellyshoal.util.compose.handleBackButton
import moe.nea.jellyshoal.views.screens.HomePage
import moe.nea.jellyshoal.views.screens.WelcomePage

// TODO: implement https://github.com/adrielcafe/voyager/issues/497
interface ShoalRoute : Screen {
	fun ownsPage(page: ShoalRoute): Boolean {
		return page == this
	}
}

val globalNavigationLocal =
	staticCompositionLocalOf<TypedNavHostController<ShoalRoute>> { error("Global Navigation Scope not provided") }

class TypedNavHostController<T : Screen>(
	val navigator: Navigator,
) {
	fun navigate(page: T) {
		navigator.push(page)
	}

	fun goBack() {
		navigator.pop()
	}

	/**
	 * **SAFETY**: this is safe assuming only navigate is ever called instead of navigator directly
	 */
	@Suppress("UNCHECKED_CAST")
	val currentScreen get() = navigator.lastItem as T
}

@Composable
fun findGlobalNavController(): TypedNavHostController<ShoalRoute> {
	return globalNavigationLocal.current
}

private val logger = KotlinLogging.logger { }

@Composable
fun NavigationContext() {
	val (accounts) = findPreference { accounts }
	logger.info { "Accounts loaded: $accounts" }
	Navigator(
		if (accounts.isEmpty()) WelcomePage
		else HomePage
	) { nav ->
		val globalNavController = TypedNavHostController<ShoalRoute>(nav)
		Column(
			modifier = Modifier.fillMaxSize()
				.handleBackButton(globalNavController)
		) { // TODO: is a column really the best container?
			CompositionLocalProvider(globalNavigationLocal provides globalNavController) {
				CurrentScreen()
			}
		}
	}
}
