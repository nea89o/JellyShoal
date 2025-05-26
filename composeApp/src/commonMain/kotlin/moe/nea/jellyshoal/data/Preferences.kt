package moe.nea.jellyshoal.data

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import moe.nea.jellyshoal.util.jellyfin.sharedJellyfinInstance
import org.jellyfin.sdk.api.client.ApiClient

class Preferences(val _store: DataStore) {
	val testValue = _store.createStringValue("server")
	val accounts =
		_store.createMapValueWithPrefix("account.")
			.mapMany(
				mapper = { props ->
					val accounts = props.keys.filter { it.startsWith("account.token.") }
						.map { it.substring("account.token.".length) }
					accounts.map {
						val token = props["account.token.$it"]!!
						Account(it, token)
					}
				},
				unmapper = { accounts ->
					val props = mutableMapOf<String, String>()
					for (account in accounts) {
						props["account.token.${account.server}"] = account.token
					}
					props
				},
			)
	val colorTheme =
		_store.createMapValueWithPrefix("colorTheme")
			.mapSingle(
				"colorTheme",
				::enumValueOf,
				SelectedColorTheme::name,
				SelectedColorTheme.SYSTEM,
			)
}

enum class SelectedColorTheme(val userFriendlyName: String) {
	LIGHT("Light Mode"), DARK("Dark Mode"), SYSTEM("Use System Color Theme"),
	;

	fun resolveToColors(): ColorScheme {
		return when (this) {
			SelectedColorTheme.LIGHT -> lightColorScheme()
			SelectedColorTheme.DARK -> darkColorScheme()
			SelectedColorTheme.SYSTEM -> lightColorScheme() // TODO: fetch system color theme somehow?
		}
	}
}

data class Account(
	val server: String,
	val token: String,
) {
	fun createApiClient(): ApiClient = sharedJellyfinInstance.createApi(
		baseUrl = server,
		accessToken = token,
	)
}

