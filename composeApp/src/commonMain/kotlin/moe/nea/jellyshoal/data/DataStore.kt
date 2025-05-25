package moe.nea.jellyshoal.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

expect object DataStore : IDataStore

abstract class IDataStore {
	fun createStringValue(key: String): DataValue<String> {
		val defaultValue = ""
		val core = createMapValueWithPrefix(key)
		return object : DataValue<String> {
			override fun getCurrent(): String {
				return core.getCurrent()[key] ?: defaultValue
			}

			override fun get(): Flow<String> {
				return core.get().map { it[key] ?: defaultValue }
			}

			override fun set(value: String) {
				core.set(mapOf(key to value))
			}
		}
	}

	abstract fun createMapValueWithPrefix(prefix: String): DataValue<Map<String, String>>
}


interface DataValue<T> {
	fun getCurrent(): T
	fun get(): Flow<T>
	fun set(value: T)

	@Composable
	fun asState(): State<T> {
		return get().collectAsStateWithLifecycle(getCurrent())
	}
}
