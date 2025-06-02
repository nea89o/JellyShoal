package moe.nea.jellyshoal.data

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import kotlinx.serialization.Transient
import moe.nea.jellyshoal.util.error.WebResult
import moe.nea.jellyshoal.util.error.explainGenericError
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
		_store.createEnumValue(
			"ui.colorTheme", SelectedColorTheme.SYSTEM,
		)

	val movieCardStyle = _store.createEnumValue(
		"ui.movieCard.style",
		MovieCardStyle.DEFAULT
	)

	val playbackControlsTimeout = _store.createFloatValue("ui.playbackControls.timeout", 2.0F)

	val playbackStartPaused = _store.createBoolValue("ui.playbackControls.startPaused", false)

	val externalVlcPath = _store.createStringValue(
		"desktop.vlc.externalPath"
	)
}

enum class MovieCardStyle(
	override val userFriendlyName: String,
) : NamedEnum {
	DEFAULT("Always use full cards"),
	COMPACT_ALWAYS("Always use compact cards"),
	COMPACT_HOME("Use compact cards only on the homescreen"),
	;

	val useCompactOnHomeScreen get() = this != DEFAULT
	val useCompactInGeneral get() = this == COMPACT_ALWAYS
}

interface NamedEnum {
	val userFriendlyName: String
}

enum class SelectedColorTheme(override val userFriendlyName: String) : NamedEnum {
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
	@Transient
	val rawApiClient by lazy {
		sharedJellyfinInstance.createApi(
			baseUrl = server,
			accessToken = token,
		)
	}

	inline fun <T> useApiClient(block: (ApiClient) -> T): WebResult<T> {
		return try {
			WebResult(block(rawApiClient))
		} catch (ex: Exception) {
			WebResult(explainGenericError(ex))
		}
	}

	// TODO: have like a central friendly name resolution name
	fun userFriendlyName(): String = server
		.replace("http://", "")
		.replace("https://", "")
}

