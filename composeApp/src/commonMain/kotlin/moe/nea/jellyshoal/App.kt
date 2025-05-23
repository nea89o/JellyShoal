package moe.nea.jellyshoal

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import moe.nea.jellyshoal.data.InjectPreferenceProvider
import moe.nea.jellyshoal.pages.NavigationContext
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(navHostController: NavHostController?) {
	InjectPreferenceProvider {
		MaterialTheme {
			NavigationContext(navHostController)
		}
	}
}
