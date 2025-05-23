package moe.nea.jellyshoal.views.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import moe.nea.jellyshoal.pages.findGlobalNavController
import moe.nea.jellyshoal.util.jellyfin.sharedJellyfinInstance
import org.jellyfin.sdk.api.client.extensions.authenticateUserByName
import org.jellyfin.sdk.api.operations.UserApi

private enum class LoginState {
	ENTERING,
	FAILURE,
	AUTHENTICATING,
}

@Composable
fun AddServerScreen(serverUrl: String) {
	val loginName = remember { mutableStateOf(TextFieldValue()) }
	val password = remember { mutableStateOf(TextFieldValue()) }
	val loginState = remember { mutableStateOf(LoginState.ENTERING) }
	val navController = findGlobalNavController()
	val scope = rememberCoroutineScope()
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center,
		modifier = Modifier.fillMaxSize(),
	) {
		Column {
			Text("Adding server", fontSize = 20.sp)
			Text("Trying to join ${serverUrl}.")
			Spacer(Modifier.height(10.dp))
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
			Spacer(Modifier.height(5.dp))
			Button(
				modifier = Modifier.align(Alignment.CenterHorizontally),
				enabled = loginState.value != LoginState.AUTHENTICATING,
				onClick = {
					loginState.value = LoginState.AUTHENTICATING
					scope.launch {
						try {
							val userApi = UserApi(sharedJellyfinInstance.createApi())
							val result by userApi.authenticateUserByName(loginName.value.text, password.value.text)
							val token = result.accessToken!!
							println("Saving token $token!")
							// navController.navigate()
						} catch (e: Exception) {
							loginState.value = LoginState.FAILURE
							// TODO: disambiguate between failure modes and display message
							e.printStackTrace()
						}
					}
				}
			) {
				Text("Log In")
			}
			// TODO: button for quick connect
		}
	}
}
