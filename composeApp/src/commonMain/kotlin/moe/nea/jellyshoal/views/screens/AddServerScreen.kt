package moe.nea.jellyshoal.views.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import moe.nea.jellyshoal.data.Account
import moe.nea.jellyshoal.data.findPreference
import moe.nea.jellyshoal.layouts.CenterColumn
import moe.nea.jellyshoal.util.ShoalRoute
import moe.nea.jellyshoal.util.error.explainHttpError
import moe.nea.jellyshoal.util.findGlobalNavController
import moe.nea.jellyshoal.util.jellyfin.sharedJellyfinInstance
import org.jellyfin.sdk.api.client.exception.InvalidStatusException
import org.jellyfin.sdk.api.client.extensions.authenticateUserByName
import org.jellyfin.sdk.api.operations.UserApi


@Serializable
data class AddServerScreen(
	val serverUrl: String,
) : ShoalRoute {
	companion object {
		val logger = KotlinLogging.logger { }
	}

	@Composable
	override fun Content() {
		val loginName = remember { mutableStateOf(TextFieldValue()) }
		val (password, setPassword) = remember { mutableStateOf(TextFieldValue()) }
		val (error, setError) = remember { mutableStateOf<String?>(null) }
		val showPassword = remember { mutableStateOf(false) }
		var isSubmitting by remember { mutableStateOf(false) }
		val navController = findGlobalNavController()
		val scope = rememberCoroutineScope()
		val accounts = findPreference { accounts }
		fun login() {
			isSubmitting = true
			scope.launch {
				try {
					setError(null)
					val userApi = UserApi(sharedJellyfinInstance.createApi(baseUrl = serverUrl))
					val result by userApi.authenticateUserByName(loginName.value.text, password.text)
					val token = result.accessToken!!
					logger.info { "Saving token $token for $serverUrl!" }
					accounts.value += Account(serverUrl, token, null)
					navController.navigate(HomePage)
				} catch (e: Exception) {
					isSubmitting = false
					val userFriendlyExplanation =
						if (e is InvalidStatusException) {
							explainHttpError(e)
						} else {
							"Failed to log in: ${e.message}"
						}
					setError(userFriendlyExplanation)
					logger.warn(e) { "Could not log in to $serverUrl" }
				}
			}
		}
		CenterColumn {
			Text("Adding server", fontSize = 20.sp)
			Text("Trying to join ${serverUrl}.")
			Spacer(Modifier.height(10.dp))
			if (error != null) {
				Text(error, color = MaterialTheme.colorScheme.error)
			}
			Spacer(Modifier.height(10.dp))
			OutlinedTextField(
				loginName.value,
				loginName::value::set,
				modifier = Modifier.padding(10.dp).fillMaxWidth(),
				singleLine = true,
				label = { Text("user name") },
				leadingIcon = { Icon(imageVector = Icons.Outlined.Person, contentDescription = null) },
				keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
				keyboardActions = KeyboardActions(onDone = { login() }),
			)
			OutlinedTextField(
				password,
				setPassword,
				visualTransformation = if (!showPassword.value) PasswordVisualTransformation() else VisualTransformation.None,
				modifier = Modifier.padding(10.dp).fillMaxWidth(),
				singleLine = true,
				label = { Text("password") },
				leadingIcon = { Icon(imageVector = Icons.Outlined.Key, contentDescription = null) },
				keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
				keyboardActions = KeyboardActions(onDone = { login() }),
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
				enabled = !isSubmitting,
				onClick = {
					login()
				},
			) {
				Icon(imageVector = Icons.AutoMirrored.Outlined.Login, contentDescription = null)
				Text("Log In")
			}
			// TODO: button for quick connect
		}
	}
}

