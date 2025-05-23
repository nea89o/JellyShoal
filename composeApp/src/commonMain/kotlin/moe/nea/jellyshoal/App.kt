package moe.nea.jellyshoal

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import moe.nea.jellyshoal.pages.NavigationContext
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
	MaterialTheme {
		NavigationContext()
	}
}
