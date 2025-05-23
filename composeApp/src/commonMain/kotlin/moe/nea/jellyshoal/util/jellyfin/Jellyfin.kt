package moe.nea.jellyshoal.util.jellyfin

import moe.nea.jellyshoal.build.BuildConfig
import org.jellyfin.sdk.createJellyfin
import org.jellyfin.sdk.model.ClientInfo
import org.jellyfin.sdk.model.DeviceInfo

val sharedJellyfinInstance = createJellyfin {
	clientInfo = ClientInfo(BuildConfig.BRAND, BuildConfig.VERSION)
	deviceInfo = DeviceInfo("some uuid stored in prefs somewhere", guessDeviceName())
}

fun guessDeviceName() = "Some Device" // TODO: get device name on something
