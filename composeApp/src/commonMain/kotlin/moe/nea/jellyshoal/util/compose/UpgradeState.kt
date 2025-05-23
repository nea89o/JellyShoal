package moe.nea.jellyshoal.util.compose

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State

/**
 * N.B.: It is the responsibility of [mutator] to update [this].
 */
fun <T> State<T>.upgrade(mutator: (T) -> Unit): MutableState<T> {
	return object : MutableState<T> {
		override var value: T
			get() = this@upgrade.value
			set(value) {
				mutator(value)
			}

		override fun component1(): T {
			return this@upgrade.value
		}

		override fun component2(): (T) -> Unit {
			return mutator
		}
	}
}
