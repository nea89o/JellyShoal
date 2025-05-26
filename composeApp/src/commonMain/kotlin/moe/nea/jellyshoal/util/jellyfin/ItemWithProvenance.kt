package moe.nea.jellyshoal.util.jellyfin

import moe.nea.jellyshoal.data.Account
import org.jellyfin.sdk.api.operations.ImageApi
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.ImageType

data class ItemWithProvenance(
	val provenance: Account,
	val item: BaseItemDto,
) {

	fun getImage(
		type: ImageType
	): String? {
		if (type !in (item.imageTags ?: emptyMap())) return null
		return ImageApi(provenance.createApiClient())
			.getItemImageUrl(
				item.id,
				imageType = type,
			)
	}
}
