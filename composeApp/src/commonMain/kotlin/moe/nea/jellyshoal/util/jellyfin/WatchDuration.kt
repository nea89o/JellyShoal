package moe.nea.jellyshoal.util.jellyfin

data class WatchDuration(val ticks: Long) {
	companion object {
		fun fromMillis(millis: Long): WatchDuration {
			return WatchDuration(millis * (10_000))
		}
	}

	operator fun times(percentage: Float): WatchDuration {
		return WatchDuration((percentage * ticks).toLong())
	}

	val asMillis get() = ticks / 1e4
	val asWholeMillis get() = asMillis.toLong()
	val asSeconds get() = asMillis / 1e3
	val asWholeSeconds get() = asSeconds.toInt()

	fun format(): String {
		val totalSecs = asWholeSeconds
		val minute = totalSecs / 60 // TODO: add hours
		val seconds = totalSecs % 60

		return "%02d:%02d".format(minute, seconds)
	}
}
