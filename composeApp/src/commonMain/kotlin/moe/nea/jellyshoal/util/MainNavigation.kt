package moe.nea.jellyshoal.util

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import io.github.oshai.kotlinlogging.KotlinLogging
import moe.nea.jellyshoal.views.screens.WelcomePage

// TODO: implement https://github.com/adrielcafe/voyager/issues/497
interface ShoalRoute : Screen {}

val globalNavigationLocal =
	staticCompositionLocalOf<TypedNavHostController<ShoalRoute>> { error("Global Navigation Scope not provided") }

class TypedNavHostController<T : Screen>(
	val navigator: Navigator,
) {
	fun navigate(page: T) {
		navigator.push(page)
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

val logger = KotlinLogging.logger { }

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NavigationContext() {
	Navigator(WelcomePage) { nav ->
		val globalNavController = TypedNavHostController<ShoalRoute>(nav)
		Column(
			modifier = Modifier.fillMaxSize().pointerInput(Unit) {
				awaitPointerEventScope {
					while (true) {
						val event = awaitPointerEvent()
						if (event.type != PointerEventType.Press) continue
						if (event.button.isBackButtonFixed && event.type == PointerEventType.Press) {
							logger.info { "Queued back event from mouse back press" }
							globalNavController.navigator.pop()
						}
					}
				}
			}
		) { // TODO: is a column really the best container?
			CompositionLocalProvider(globalNavigationLocal provides globalNavController) {
				CurrentScreen()
			}
		}
	}
}

val PointerButton?.isBackButtonFixed
	get() = this?.index == 5 || this?.index == 3
val PointerButton?.isForwardButtonFixed
	get() = this?.index == 6 || this?.index == 4
