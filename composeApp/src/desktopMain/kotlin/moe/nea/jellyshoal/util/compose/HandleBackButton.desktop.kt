package moe.nea.jellyshoal.util.compose

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import io.github.oshai.kotlinlogging.KotlinLogging
import moe.nea.jellyshoal.util.TypedNavHostController

private val logger = KotlinLogging.logger { }

@OptIn(ExperimentalComposeUiApi::class)
actual fun Modifier.handleBackButton(navController: TypedNavHostController<*>): Modifier {
	return this.pointerInput(Unit) {// TODO: expect/actual this to add in the back button
		awaitPointerEventScope {
			while (true) {
				val event = awaitPointerEvent()
				if (event.type != PointerEventType.Press) continue
				if (event.button.isBackButtonFixed && event.type == PointerEventType.Press) {
					logger.info { "Queued back event from mouse back press" }
					navController.goBack()
				}
			}
		}
	}
}


val PointerButton?.isBackButtonFixed
	get() = this?.index == 5 || this?.index == 3
val PointerButton?.isForwardButtonFixed
	get() = this?.index == 6 || this?.index == 4

