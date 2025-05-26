package moe.nea.jellyshoal.data

import dev.dirs.ProjectDirectories
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import moe.nea.jellyshoal.build.BuildConfig
import java.io.File
import java.util.*


@OptIn(DelicateCoroutinesApi::class)
actual object DataStore :
	IDataStore() { // TODO: this could potentially be a class that is injected into the local context using a provider.
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

	fun readProps(): Map<String, String> {
		val props = Properties()
		logger.info { "Loading config from $configFile" }
		try {
			configFile.reader().use { props.load(it) }
		} catch (e: Exception) {
			logger.error(e) { "Failed to load config file" }
		}
		@Suppress("UNCHECKED_CAST")
		return props as Map<String, String>
	}

	val propFlow = MutableStateFlow(readProps())

	fun saveConfig(map: Map<String, String>) {
		configFile.writer().use {
			val props = Properties()
			props.putAll(map)
			props.store(it, "Configuration and logins for ${BuildConfig.BRAND}.")
		}
	}

	init {
		// TODO. replace global scope with some sort of application context scope (and analogously turn object into class)
		GlobalScope.launch {
			propFlow.collect { saveConfig(it) }
		}
	}

	override fun createMapValueWithPrefix(prefix: String): DataValue<Map<String, String>> {
		fun filterProps(map: Map<String, String>) = map.filterKeys { it.startsWith(prefix) }
		val flow = propFlow.map { filterProps(it) }
		return object : DataValue<Map<String, String>> {
			override fun getCurrent(): Map<String, String> {
				return filterProps(propFlow.value)
			}

			override fun get(): Flow<Map<String, String>> {
				return flow
			}

			override fun set(value: Map<String, String>) {
				while (true) { // A CAS loop? in my simple property based file storage? it is more likely than you think.
					val propsD = propFlow.value
					val p = filterProps(propsD)
					val propsM = propsD.toMutableMap()
					for (key in p.keys) {
						if (key in value) {
							propsM.remove(key)
						}
					}
					propsM.putAll(value)
					if (propFlow.compareAndSet(propFlow.value, propsM))
						break
				}
			}
		}
	}
}
