package moe.nea.jellyshoal.util.jellyfin

import androidx.compose.runtime.Immutable
import moe.nea.jellyshoal.data.Account
import org.jellyfin.sdk.api.operations.ImageApi
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.ImageType

@Immutable
data class ItemWithProvenance(
	val provenance: Account,
	val item: BaseItemDto,
) {

	fun getWatchProgress(): WatchProgress? {
		val totalTicks = item.runTimeTicks ?: return null
		val watchedTicks = item.userData?.playbackPositionTicks ?: return null

		return WatchProgress(WatchDuration(watchedTicks), WatchDuration(totalTicks))
	}

	fun getImage(
		type: ImageType,
		highQuality: Boolean = false,
	): String? {
		if (type !in (item.imageTags ?: emptyMap())) return null
		return ImageApi(provenance.createApiClient())
			.getItemImageUrl(
				item.id,
				imageType = type,
				quality = if (highQuality) 100 else null,
			)
	}
}

fun BaseItemDto.withProvenance(account: Account) =
	ItemWithProvenance(account, this)
