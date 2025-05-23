package moe.nea.jellyshoal.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import kotlinx.coroutines.flow.Flow

expect object DataStore {
	fun createStringValue(key: String): DataValue<String>
}


interface DataValue<T> {
	@Composable
	fun asState(): State<T>
	fun get(): Flow<T>
	fun set(value: T)
}
