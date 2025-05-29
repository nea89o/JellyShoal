package moe.nea.jellyshoal.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Fullscreen
import androidx.compose.material.icons.outlined.FullscreenExit
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.delay
import moe.nea.jellyshoal.data.findPreference
import moe.nea.jellyshoal.util.compose.findFullScreenHelper
import moe.nea.jellyshoal.util.compose.toggleFullScreen
import moe.nea.jellyshoal.util.findGlobalNavController
import moe.nea.jellyshoal.util.jellyfin.WatchDuration
import moe.nea.jellyshoal.util.jellyfin.WatchProgress
import moe.nea.jellyshoal.util.vlc.SkiaBitmapFormatCallback
import moe.nea.jellyshoal.util.vlc.SkiaBitmapRenderCallback
import moe.nea.jellyshoal.util.vlc.findMediaPlayerComponent
import org.jetbrains.skia.Bitmap
import uk.co.caprica.vlcj.binding.lib.LibVlc
import uk.co.caprica.vlcj.binding.support.init.LinuxNativeInit
import uk.co.caprica.vlcj.media.Media
import uk.co.caprica.vlcj.media.MediaEventAdapter
import uk.co.caprica.vlcj.media.MediaParsedStatus
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.base.State
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent
import uk.co.caprica.vlcj.player.component.MediaPlayerSpecs
import kotlin.time.Duration.Companion.seconds

private val logger = KotlinLogging.logger {}

enum class PauseState {
	PAUSED,
	PLAYING,
	// STOPPED,

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun VideoPlayer(
	url: String,
	modifier: Modifier,
) {
	val bitmap = remember { mutableStateOf<Bitmap?>(null) }
	val mediaPlayerComponent = remember { findMediaPlayerComponent(bitmap) }
	val player = remember { mediaPlayerComponent.mediaPlayer() }
	var totalDuration: WatchDuration? by remember { mutableStateOf(null) }
	var isPaused by remember { mutableStateOf(PauseState.PLAYING) }
	var playbackPosition by remember { mutableStateOf(WatchDuration(0L)) }
	var lastActivityGeneration by remember { mutableStateOf(0L) }
	var isOverlayVisible by remember { mutableStateOf(true) }
	val playbackControlsTimeout by findPreference { playbackControlsTimeout }
	val playbackStartPaused by findPreference { playbackStartPaused }
	LaunchedEffect(lastActivityGeneration) {
		delay(playbackControlsTimeout.toDouble().seconds)
		isOverlayVisible = false
	}

	LaunchedEffect(url) {
		logger.info { "Loading url $url" }
		player.events().addMediaEventListener(object : MediaEventAdapter() {
			override fun mediaStateChanged(media: Media?, newState: State?) {
				logger.info { "Media player state changed to $newState" }
				LibVlc.libvlc_errmsg()?.let { errorMsg ->
					logger.error { "Media player errored: $errorMsg" }
				}
			}

			override fun mediaParsedChanged(
				media: Media,
				newStatus: MediaParsedStatus
			) {
				if (newStatus == MediaParsedStatus.DONE) {
					totalDuration = WatchDuration.fromMillis(media.info().duration())
				}
				logger.info { "Media player parsed to $newStatus" }
			}
		})
		player.events().addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {
			override fun error(mediaPlayer: MediaPlayer?) {
				LibVlc.libvlc_errmsg()?.let { errorMsg ->
					logger.error { "Media player errored: $errorMsg" }
				}
			}

			override fun playing(mediaPlayer: MediaPlayer?) {
				isPaused = PauseState.PLAYING
			}

			override fun paused(mediaPlayer: MediaPlayer?) {
				isPaused = PauseState.PAUSED
			}

			override fun stopped(mediaPlayer: MediaPlayer?) {
				isPaused = PauseState.PAUSED // TODO: stopped
			}

			override fun timeChanged(mediaPlayer: MediaPlayer?, newTime: Long) {
				val watchDuration = WatchDuration.fromMillis(newTime)
				logger.trace { "Time changed to $watchDuration" }
				playbackPosition = watchDuration
			}
		})
		if (playbackStartPaused) {
			player.media().startPaused(url)
		} else {
			player.media().start(url)
		}
		// TODO: can vlc4j seek?
	}
	DisposableEffect(Unit) {
		onDispose {
			logger.info { "Deleting player" }
			player.release()
		}
	}

	fun togglePause() {
		if (isPaused == PauseState.PAUSED) player.controls().play()
		else player.controls().pause()
	}

	val focusRequester = remember { FocusRequester() }
	LaunchedEffect(Unit) {
		focusRequester.requestFocus()
	}
	Box(
		modifier.background(Color.Black)
			.fillMaxSize()
			.onPointerEvent(PointerEventType.Move) {
				isOverlayVisible = true
				lastActivityGeneration = System.currentTimeMillis()
			}
			.focusRequester(focusRequester)
			.focusable()
			.onKeyEvent {
				if (it.key == Key.Spacebar && it.type == KeyEventType.KeyDown) {
					togglePause()
					logger.info { "Pressed pause space bar" }
					return@onKeyEvent true
				}
				false
			}
	) {
		Canvas(modifier = Modifier.fillMaxSize()) {
			bitmap.value?.let { bmp ->
				val imageAspectRatio = bmp.width.toFloat() / bmp.height.toFloat()
				val screenAspectRatio = size.width / size.height
				val scaledImageSize: IntSize
				val imageOffset: IntOffset
				logger.trace {
					val lastLastCall = lastCall
					lastCall = System.currentTimeMillis()
					"Received new frame after ${lastCall - lastLastCall}ms"
				}

				if (imageAspectRatio > screenAspectRatio) {
					// The image is wider than the screen
					scaledImageSize =
						IntSize(
							size.width.toInt(),
							(size.width / imageAspectRatio).toInt()
						)
					imageOffset = IntOffset(
						0,
						((size.height - scaledImageSize.height) / 2).toInt()
					)
				} else {
					// The image is higher 🌿 than the screen
					scaledImageSize =
						IntSize(
							(size.height * imageAspectRatio).toInt(),
							size.height.toInt(),
						)
					imageOffset = IntOffset(
						((size.width - scaledImageSize.width) / 2).toInt(),
						0
					)

				}
				drawImage(
					bmp.asComposeImageBitmap(),
					dstOffset = imageOffset,
					dstSize = scaledImageSize,
				)
			}
		}
		AnimatedVisibility(
			isOverlayVisible,
			modifier = Modifier.fillMaxSize(),
			enter = fadeIn(animationSpec = tween(durationMillis = 200)),
			exit = fadeOut(animationSpec = tween(durationMillis = 600)),
		) {
			CompositionLocalProvider(LocalContentColor provides Color.White) {
				ProvideTextStyle(value = LocalTextStyle.current.copy(color = Color.White)) {
					Column(
						verticalArrangement = Arrangement.SpaceBetween,
						modifier = Modifier.matchParentSize(),
					) {
						Row(
							modifier = Modifier.padding(16.dp),
							verticalAlignment = Alignment.CenterVertically,
						) { // Top Row
							val nav = findGlobalNavController()
							IconButton(onClick = {
								nav.goBack()
							}) {
								Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
							}
							Text(
								"Hier könnte ihr Film Titel stehen",
								style = MaterialTheme.typography.h5
							)
						}
						Row(
							modifier = Modifier.padding(16.dp),
							verticalAlignment = Alignment.CenterVertically,
						) { // Bottom Row
							IconButton(onClick = {
								togglePause()
							}) {
								if (isPaused == PauseState.PAUSED)
									Icon(Icons.Outlined.PlayArrow, contentDescription = "Play")
								else Icon(Icons.Outlined.Pause, contentDescription = "Pause")
							}
							Text(playbackPosition.format(), modifier = Modifier.padding(8.dp))
							val wp = WatchProgress.fromLoadingTimespan(playbackPosition, totalDuration)
							var progressBarWidth by remember { mutableStateOf(0) }
							val progressMod = Modifier.padding(8.dp).weight(1F)
								.height(12.dp)
								.onGloballyPositioned {
									progressBarWidth = it.size.width
								}
								.pointerInput(Unit) {
									detectTapGestures { position ->
										if (progressBarWidth > 0) {
											val percentage = (position.x / progressBarWidth).coerceIn(0F, 1F)
											val td = totalDuration ?: return@detectTapGestures
											val viewPosition = (td * percentage)
											if (isPaused != PauseState.PLAYING) {
												playbackPosition =
													viewPosition // timeChanged only gets called while playing
											}
											playbackPosition = viewPosition
											logger.info { "Seeking to $viewPosition" }
											player.controls()
												.setTime(viewPosition.asWholeMillis)
										}
									}
									// TODO: really cool and awesome dragging
								}
							val cap = StrokeCap.Round
							if (wp == null) LinearProgressIndicator(modifier = progressMod, strokeCap = cap)
							else LinearProgressIndicator(
								modifier = progressMod,
								progress = wp.progress,
								strokeCap = cap
							)
							Text(totalDuration?.format() ?: "--:--", modifier = Modifier.padding(8.dp))

							val fullscreenHelper = findFullScreenHelper()
							IconButton(
								onClick = {
									fullscreenHelper.toggleFullScreen()
								}
							) {
								if (fullscreenHelper.isFullScreen)
									Icon(Icons.Outlined.FullscreenExit, contentDescription = "Exit Fullscreen")
								else
									Icon(Icons.Outlined.Fullscreen, contentDescription = "Enter Fullscreen")
							}
						}
					}
				}
			}
		}
	}
}

var lastCall = System.currentTimeMillis()

