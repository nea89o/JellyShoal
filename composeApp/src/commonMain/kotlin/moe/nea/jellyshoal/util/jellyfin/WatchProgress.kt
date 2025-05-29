package moe.nea.jellyshoal.util.jellyfin

data class WatchProgress(
	val current: WatchDuration,
	val total: WatchDuration,
) {
	val progress: Float = current.ticks.toFloat() / total.ticks.toFloat()

	companion object {
		fun fromLoadingTimespan(current: WatchDuration?, total: WatchDuration?): WatchProgress? {
			if (current == null) return null
			if (total == null) return null
			return WatchProgress(current, total)
		}
	}
}
