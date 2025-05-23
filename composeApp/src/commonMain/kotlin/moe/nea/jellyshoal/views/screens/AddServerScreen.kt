package moe.nea.jellyshoal.views.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jellyfin.sdk.createJellyfin

@Composable
fun AddServerScreen(serverUrl: String) {
	val loginName = remember { mutableStateOf(TextFieldValue()) }
	val password = remember { mutableStateOf(TextFieldValue()) }
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center,
		modifier = Modifier.fillMaxSize(),
	) {
		Column {
			Text("Adding server", fontSize = 20.sp)
			Text("Trying to join ${serverUrl}.")
			Box(Modifier.height(10.dp))
			TextField(
				loginName.value,
				loginName::value::set,
				label = { Text("user name") },
//				leadingIcon = { Icon(MaterialIcons) }
			)
			TextField(
				password.value,
				password::value::set,
				label = { Text("password") },
			)
			Box(Modifier.height(5.dp))
			Button(
				modifier = Modifier.align(Alignment.CenterHorizontally),
				onClick = {
					val jellyfin = createJellyfin {
						
					}
				}
			) {
				Text("Log In")
			}
			// TODO: button for quick connect
		}
	}
}
