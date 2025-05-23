package moe.nea.jellyshoal

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.isTraySupported
import java.awt.SystemTray

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "JellyShoal",
    ) {
        App()
    }
}
