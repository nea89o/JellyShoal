package moe.nea.jellyshoal.views.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import moe.nea.jellyshoal.pages.HomePage
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
	val (password, setPassword) = remember { mutableStateOf(TextFieldValue()) }
	val loginState = remember { mutableStateOf(LoginState.ENTERING) }
	val showPassword = remember { mutableStateOf(false) }
	val navController = findGlobalNavController()
	val scope = rememberCoroutineScope()
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = Modifier.fillMaxSize(),
	) {
		Column(
			verticalArrangement = Arrangement.Center,
			modifier = Modifier.fillMaxHeight()
		) {
			Text("Adding server", fontSize = 20.sp)
			Text("Trying to join ${serverUrl}.")
			Spacer(Modifier.height(10.dp))
			OutlinedTextField(
				loginName.value,
				loginName::value::set,
				modifier = Modifier.padding(10.dp),
				label = { Text("user name") },
				leadingIcon = { Icon(imageVector = Icons.Outlined.Person, contentDescription = null) },
			)
			OutlinedTextField(
				password,
				setPassword,
				visualTransformation = if (!showPassword.value) PasswordVisualTransformation() else VisualTransformation.None,
				modifier = Modifier.padding(10.dp),
				label = { Text("password") },
				leadingIcon = { Icon(imageVector = Icons.Outlined.Key, contentDescription = null) },
				trailingIcon = {
					IconButton(onClick = {
						showPassword.value = !showPassword.value
					}) {
						Icon(
							if (showPassword.value) Icons.Outlined.VisibilityOff
							else Icons.Outlined.Visibility,
							contentDescription = if (showPassword.value) "Hide Password"
							else "Show Password"
						)
					}
				}
			)
			Spacer(Modifier.height(5.dp))
			Button(
				modifier = Modifier.align(Alignment.CenterHorizontally),
				enabled = loginState.value != LoginState.AUTHENTICATING,
				onClick = {
					loginState.value = LoginState.AUTHENTICATING
					scope.launch {
						try {
							val userApi = UserApi(sharedJellyfinInstance.createApi(baseUrl = serverUrl))
							val result by userApi.authenticateUserByName(loginName.value.text, password.text)
							val token = result.accessToken!!
							println("Saving token $token for $serverUrl!")
							navController.navigate(HomePage)
						} catch (e: Exception) {
							loginState.value = LoginState.FAILURE
							// TODO: disambiguate between failure modes and display message
							e.printStackTrace()
						}
					}
				}
			) {
				Icon(imageVector = Icons.AutoMirrored.Outlined.Login, contentDescription = null)
				Text("Log In")
			}
			// TODO: button for quick connect
		}
	}
}
