package moe.nea.jellyshoal.util.error

import org.jellyfin.sdk.api.client.exception.InvalidStatusException

fun explainHttpError(e: InvalidStatusException): String {
	val section = e.status / 100
	return when (e.status) {
		502 -> "A connection could be established, but a backend system is unavailable (${e.status} Bad Gateway)"
		500 -> "Unknown Internal server error (500 Internal Server Error)"
		503 -> "This service is unavailable (503 Service Unavailable)"
		400 -> "Missing or invalid data (400 Bad Request)"
		401 -> "Failed authentication (401 Unauthorized)"
		403 -> "Insufficient authentication (403 Forbidden)"
		else -> when (section) {
			4 -> "Generic user error (${e.status})"
			5 -> "Server error (${e.status})"
			else -> "Unknown HTTP error (${e.status})"
		}
	}

}

