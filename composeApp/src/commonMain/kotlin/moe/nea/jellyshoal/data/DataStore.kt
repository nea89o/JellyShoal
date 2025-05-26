package moe.nea.jellyshoal.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

expect object DataStore : IDataStore

abstract class IDataStore {
	fun createStringValue(key: String): DataValue<String> {
		return createMapValueWithPrefix(key).mapSingle(key, { it }, { it }, "")
	}

	abstract fun createMapValueWithPrefix(prefix: String): DataValue<Map<String, String>>
}

inline fun <reified E : Enum<E>> IDataStore.createEnumValue(key: String, defaultValue: E): DataValue<E> {
	return createMapValueWithPrefix(key)
		.mapSingle(
			key,
			::enumValueOf,
			{ it.name },
			defaultValue
		)
}

fun <T> DataValue<Map<String, String>>.mapSingle(
	key: String,
	mapper: (String) -> T,
	unmapper: (T) -> String,
	defaultValue: T
): DataValue<T> {
	val core = this
	return object : DataValue<T> {
		override fun getCurrent(): T {
			return core.getCurrent()[key]?.let(mapper) ?: defaultValue
		}

		override fun get(): Flow<T> {
			return core.get().map { it[key]?.let(mapper) ?: defaultValue }
		}

		override fun set(value: T) {
			core.set(mapOf(key to unmapper(value)))
		}
	}
}

fun <T> DataValue<Map<String, String>>.mapMany(
	mapper: (Map<String, String>) -> T,
	unmapper: (T) -> Map<String, String>,
): DataValue<T> {
	val core = this
	return object : DataValue<T> {
		override fun getCurrent(): T {
			return core.getCurrent().let(mapper)
		}

		override fun get(): Flow<T> {
			return core.get().map { it.let(mapper) }
		}

		override fun set(value: T) {
			core.set(unmapper(value))
		}
	}
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
