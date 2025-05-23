package moe.nea.jellyshoal.pages

import kotlinx.serialization.Serializable

@Serializable
data class AddServerPage(
	val serverUrl: String,
)
