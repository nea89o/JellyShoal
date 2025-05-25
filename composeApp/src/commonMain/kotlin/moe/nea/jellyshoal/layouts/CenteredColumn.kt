package moe.nea.jellyshoal.layouts

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun CenterColumn(
	width: Dp = 350.dp,
	content: @Composable ColumnScope.() -> Unit
) {
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = Modifier.fillMaxSize(),
	) {
		Column(
			verticalArrangement = Arrangement.Center,
			modifier = Modifier.fillMaxHeight().padding(8.dp).width(width)
		) {
			content()
		}
	}
}
