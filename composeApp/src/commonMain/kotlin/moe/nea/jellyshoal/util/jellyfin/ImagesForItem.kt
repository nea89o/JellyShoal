package moe.nea.jellyshoal.util.jellyfin

import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.ImageType
import org.jellyfin.sdk.model.api.request.GetItemImageRequest

fun BaseItemDto.findImage(imageType: ImageType): GetItemImageRequest? {
	val tag = this.imageTags?.get(imageType) ?: return null

	return GetItemImageRequest(
		itemId = this.id,
		imageType = imageType,
		tag = tag
	)
}
