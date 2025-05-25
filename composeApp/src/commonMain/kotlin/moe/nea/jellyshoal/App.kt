package moe.nea.jellyshoal

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import moe.nea.jellyshoal.data.InjectPreferenceProvider
import moe.nea.jellyshoal.util.NavigationContext
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
	InjectPreferenceProvider {
		MaterialTheme {
			NavigationContext()
		}
	}
}
