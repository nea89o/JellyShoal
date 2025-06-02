package moe.nea.jellyshoal.util.error

class WebResult<T> private constructor(
	val result: T?,
	val errorMessage: String? = null,
) {
	constructor(result: T) : this(result, null)
	constructor(errorMessage: String) : this(null, errorMessage)

	inline fun <R> handle(left: (T) -> R, right: (String) -> R): R {
		return if (errorMessage != null)
			right(errorMessage)
		else
			left(result as T)
	}

	fun unsafeGetResult(): T {
		if (errorMessage != null) {
			throw Exception(errorMessage) // TODO: preserve actual exception
		}
		return result as T
	}
}
