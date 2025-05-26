package moe.nea.jellyshoal

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import moe.nea.jellyshoal.data.InjectPreferenceProvider
import moe.nea.jellyshoal.data.findPreference
import moe.nea.jellyshoal.util.NavigationContext
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
	InjectPreferenceProvider {
		val (darkMode) = findPreference { colorTheme }
		MaterialTheme(colorScheme = darkMode.resolveToColors()) {
			NavigationContext()
		}
	}
}
