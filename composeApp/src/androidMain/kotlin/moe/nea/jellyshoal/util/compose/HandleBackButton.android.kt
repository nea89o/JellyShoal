package moe.nea.jellyshoal.util.compose

import androidx.compose.ui.Modifier
import moe.nea.jellyshoal.util.TypedNavHostController

actual fun Modifier.handleBackButton(navController: TypedNavHostController<*>): Modifier {
	return this
}
