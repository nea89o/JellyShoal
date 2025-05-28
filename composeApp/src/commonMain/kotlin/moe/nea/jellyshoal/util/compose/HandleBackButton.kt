package moe.nea.jellyshoal.util.compose

import androidx.compose.ui.Modifier
import moe.nea.jellyshoal.util.TypedNavHostController


expect fun Modifier.handleBackButton(navController: TypedNavHostController<*>): Modifier
