package moe.nea.jellyshoal.util.error

import org.jellyfin.sdk.api.client.exception.InvalidStatusException

fun explainGenericError(ex: Exception): String {
	if (ex is InvalidStatusException)
		return explainHttpError(ex)
	return ex.message!!
}
