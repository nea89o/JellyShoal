package moe.nea.jellyshoal.util.jellyfin

import moe.nea.jellyshoal.data.Account
import org.jellyfin.sdk.api.operations.ImageApi
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.ImageType

data class WatchDuration(val ticks: Long) {
	val asSeconds get() = ticks / 1e7
	val asWholeSeconds get() = asSeconds.toInt()

	fun format(): String {
		val totalSecs = asWholeSeconds
		val minute = totalSecs / 60 // TODO: add hours
		val seconds = totalSecs % 60

		return "%02d:%02d".format(minute, seconds)
	}
}

data class MovieProgress(
	val current: WatchDuration,
	val total: WatchDuration,
) {
	val progress: Float = current.ticks.toFloat() / total.ticks.toFloat()
}

data class ItemWithProvenance(
	val provenance: Account,
	val item: BaseItemDto,
) {

	fun getWatchProgress(): MovieProgress? {
		val totalTicks = item.runTimeTicks ?: return null
		val watchedTicks = item.userData?.playbackPositionTicks ?: return null

		return MovieProgress(WatchDuration(watchedTicks), WatchDuration(totalTicks))
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
