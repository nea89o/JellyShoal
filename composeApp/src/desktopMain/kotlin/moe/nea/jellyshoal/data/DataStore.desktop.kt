package moe.nea.jellyshoal.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.dirs.ProjectDirectories
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import moe.nea.jellyshoal.build.BuildConfig
import java.io.File
import java.util.*


actual object DataStore { // TODO: this could potentially be a class that is injected into the local context using a provider.
	val logger = KotlinLogging.logger {}
	val paths = ProjectDirectories.from(
		BuildConfig.GROUP_QUALIFIER,
		BuildConfig.PUBLISHER,
		BuildConfig.BRAND
	)
	val configDir = File(paths.configDir).also {
		it.mkdirs()
	}
	val configFile = configDir.resolve("config.properties")

	val props = run {
		val props = Properties()
		logger.info { "Loading config from ${configFile}" }
		try {
			configFile.reader().use { props.load(it) }
		} catch (e: Exception) {
			logger.error(e) { "Failed to load config file" }
		}
		props
	}

	fun saveConfig() {

		configFile.writer().use {
			props.store(it, "Configuration and logins for ${BuildConfig.BRAND}. Saved at ${Date()}.")
		}
	}

	actual fun createStringValue(key: String): DataValue<String> {
		val defaultValue = ""
		val flow = MutableStateFlow(props.getProperty(key) ?: defaultValue)
		return object : DataValue<String> {
			@Composable
			override fun asState(): State<String> {
				return get().collectAsStateWithLifecycle(props.getProperty(key, defaultValue))
			}

			override fun get(): Flow<String> {
				return flow
			}

			override fun set(value: String) {
				flow.value = value
				props[key] = value
				saveConfig() // TODO: dispatch to another thread for writing? or only on application exit
			}
		}
	}
}
