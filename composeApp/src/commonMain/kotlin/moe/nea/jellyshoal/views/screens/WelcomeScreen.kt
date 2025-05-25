package moe.nea.jellyshoal.views.screens

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.sp
import kotlinx.serialization.Serializable
import moe.nea.jellyshoal.data.findPreference
import moe.nea.jellyshoal.layouts.CenterColumn
import moe.nea.jellyshoal.util.ShoalRoute
import moe.nea.jellyshoal.util.findGlobalNavController

@Serializable
object WelcomePage : ShoalRoute {
	@Composable
	override fun Content() {
		val nav = findGlobalNavController()
		CenterColumn {
			Text("Welcome to JellyShoal", fontSize = 20.sp)
			Text("Test Page, Please add more content")
			Button(onClick = {
				nav.navigate(SelectServerPage)
			}) {
				Text("Continue")
			}

		}
	}
}

@Serializable
object SelectServerPage : ShoalRoute {
	@Composable
	override fun Content() {
		var testValue by findPreference { testValue }
		var server by remember { mutableStateOf(TextFieldValue(testValue)) }
		val navController = findGlobalNavController()
		fun login() {
			// TODO: validate url
			val serverUrl = server.text
			navController.navigate(AddServerScreen(serverUrl))
			testValue = server.text
		}
		CenterColumn {
			Text("Select Server", fontSize = 20.sp)
			Text("You currently don't have a server.")
			OutlinedTextField(
				server, { server = it }, singleLine = true,
				keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
				keyboardActions = KeyboardActions(onDone = { login() }),
				leadingIcon = {
					Icon(
						Icons.Outlined.Dns,
						contentDescription = "Server Address"
					)
				}
			)
			Button(onClick = {
				login()
			}) {
				Text("Go to Login")
			}
		}
	}
}

