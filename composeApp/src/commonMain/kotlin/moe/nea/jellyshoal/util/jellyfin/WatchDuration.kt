package moe.nea.jellyshoal.util.jellyfin

data class WatchDuration(val ticks: Long) {
	companion object {
		fun fromMillis(millis: Long): WatchDuration {
			return WatchDuration(millis * (10_000_000 / 1000))
		}
	}

	val asSeconds get() = ticks / 1e7
	val asWholeSeconds get() = asSeconds.toInt()

	fun format(): String {
		val totalSecs = asWholeSeconds
		val minute = totalSecs / 60 // TODO: add hours
		val seconds = totalSecs % 60

		return "%02d:%02d".format(minute, seconds)
	}
}
