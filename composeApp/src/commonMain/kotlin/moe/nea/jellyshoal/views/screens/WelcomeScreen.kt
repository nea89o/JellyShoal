package moe.nea.jellyshoal.views.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import moe.nea.jellyshoal.pages.AddServerPage
import moe.nea.jellyshoal.pages.SettingsPage
import moe.nea.jellyshoal.pages.findGlobalNavController

private enum class Step {
	Welcome,
	GoToLogin,
}

@Composable
fun WelcomeView() {
	val page = remember { mutableStateOf(Step.Welcome) }
	val server = remember { mutableStateOf(TextFieldValue()) }
	val navController = findGlobalNavController()
	fun login() {
		// TODO: validate url
		val serverUrl = server.value.text
		navController.navigate(AddServerPage(serverUrl))
	}
	Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize().padding(8.dp)) {
		when (page.value) {
			Step.Welcome -> {
				Text("Welcome to JellyShoal", fontSize = 20.sp)
				Text("Test Page, Please add more content")
				Button(onClick = {
					page.value = Step.GoToLogin
				}) {
					Text("Continue")
				}
			}

			Step.GoToLogin -> {
				Text("Select Server", fontSize = 20.sp)
				Text("You currently don't have a server.")
				OutlinedTextField(
					server.value, server::value::set, singleLine = true,
					keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
					keyboardActions = KeyboardActions(onDone = { login() }),
				)
				Button(onClick = {
					login()
				}) {
					Text("Go to Login")
				}
			}
		}
	}
}
