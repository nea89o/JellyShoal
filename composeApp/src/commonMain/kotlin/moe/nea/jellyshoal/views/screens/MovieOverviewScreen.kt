package moe.nea.jellyshoal.views.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import moe.nea.jellyshoal.components.DownloadManager
import moe.nea.jellyshoal.data.Account
import moe.nea.jellyshoal.layouts.CenterColumn
import moe.nea.jellyshoal.layouts.DefaultSideBar
import moe.nea.jellyshoal.util.ShoalRoute
import moe.nea.jellyshoal.util.error.WebResult
import moe.nea.jellyshoal.util.findGlobalNavController
import moe.nea.jellyshoal.util.jellyfin.ItemWithProvenance
import moe.nea.jellyshoal.util.jellyfin.withProvenance
import org.jellyfin.sdk.api.client.extensions.userLibraryApi
import org.jellyfin.sdk.api.client.extensions.videosApi
import org.jellyfin.sdk.model.UUID
import org.jellyfin.sdk.model.api.ImageType
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.lang.Runnable
import java.net.URL
import javax.swing.JFileChooser
import javax.swing.SwingUtilities
import javax.swing.filechooser.FileNameExtensionFilter
import kotlin.coroutines.CoroutineContext

private val logger = KotlinLogging.logger { }

data class MovieOverviewScreen(
	val account: Account,
	val itemId: UUID,
) : ShoalRoute {
	@Composable
	override fun Content() {
		var item: WebResult<ItemWithProvenance>? by remember { mutableStateOf(null) }

		LaunchedEffect(account, itemId) {
			item = account.useApiClient {
				it.userLibraryApi
					.getItem(itemId = itemId)
					.content
					.withProvenance(account)
			}
			// TODO: is there user review stuff here?
		}

		DefaultSideBar {
			item?.handle(
				{ item ->
					Row(modifier = Modifier.fillMaxSize().padding(8.dp)) {
						Column(modifier = Modifier.weight(1f)) {
							AsyncImage(
								item.getImage(ImageType.PRIMARY, highQuality = true)!!,
								null,
								modifier = Modifier.fillMaxWidth()
							)
						}
						Column(modifier = Modifier.weight(2f)) {
							Text(
								item.item.name!!,
								Modifier.padding(16.dp),
								style = MaterialTheme.typography.headlineLarge
							)
							Text(
								item.item.overview ?: "",
								Modifier.padding(16.dp),
								style = MaterialTheme.typography.bodyLarge
							)
							Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
								Row {}
								Row {
									val nav = findGlobalNavController()
									// TODO: check progress and show a resume button instead
									Button(
										onClick = {
											nav.navigate(
												PlayVideoScreen(item)
											)
										}
									) {
										Icon(Icons.Outlined.PlayArrow, contentDescription = "Play")
										Text("Play")
									}
								}
								Row(horizontalArrangement = Arrangement.End) {
									Button(onClick = {
										GlobalScope.launch {
											performDownload(item)
										}
									}) {
										Icon(Icons.Outlined.Download, contentDescription = "Download")
									}
								}
							}
						}
					}
				},
				{
					BigErrorPage(it)
				}
			)
		}
	}

	companion object {
		fun from(item: ItemWithProvenance): ShoalRoute {
			return MovieOverviewScreen(
				item.provenance, item.item.id
			)
		}
	}
}

val swingDispatcher = object : CoroutineDispatcher() {
	override fun dispatch(context: CoroutineContext, block: Runnable) {
		if (SwingUtilities.isEventDispatchThread()) {
			block.run()
		} else {
			SwingUtilities.invokeLater(block)
		}
	}
}

suspend fun performDownload(item: ItemWithProvenance) {
	val fileChooser = JFileChooser()
	fileChooser.dialogTitle = "Download MP4"
	fileChooser.selectedFile = File("${item.item.name}")
	val mp4Filter = FileNameExtensionFilter("MP4", "mp4")
	val mkvFilter = FileNameExtensionFilter("Matroska", "mkv")
	fileChooser.addChoosableFileFilter(mp4Filter)
	fileChooser.addChoosableFileFilter(mkvFilter)
	fileChooser.fileFilter = mp4Filter
	if (withContext(swingDispatcher) { fileChooser.showSaveDialog(null) } == JFileChooser.APPROVE_OPTION) {
		val containerType = when (fileChooser.fileFilter) {
			mkvFilter -> "mkv"
			mp4Filter -> "mp4"
			else -> "mp4"
		}
		val downloadUrl = item.provenance.useApiClient {
			it.videosApi
				.getVideoStreamUrl(
					itemId = item.item.id,
					container = containerType,
					static = true,
				)
		}.unsafeGetResult()
		logger.info { "Downloading movie from $downloadUrl" }
		var outputFile = fileChooser.selectedFile
		if (outputFile.extension != containerType)
			outputFile =
				outputFile.resolveSibling(outputFile.nameWithoutExtension + "." + containerType)
		outputFile.parentFile.mkdirs()
		val ref = DownloadManager.create(outputFile.name)
		// TODO: show download progress somewhere in sidebar most likely
		try {
			withContext(Dispatchers.IO) {
				URL(downloadUrl).openConnection().let { conn ->
					val expectedLength = conn.contentLengthLong
					conn.inputStream.use { inputStream ->
						outputFile.outputStream().use { outputStream ->
							inputStream.copyWithProgress(outputStream) {
								if (DownloadManager.isCancelled(ref)) {
									throw Cancellation()
								}
								DownloadManager.updateProgress(ref, (it / expectedLength.toDouble()).toFloat())
							}
						}
					}
				}
			}
		} catch (ex: Cancellation) {
			outputFile.delete()
			logger.warn { "Cancelled download of $outputFile" }
			return
		}
		DownloadManager.finish(ref)
		logger.info { "Downloaded movie to ${outputFile}" }
	}
}

private class Cancellation() : Exception()

inline fun InputStream.copyWithProgress(to: OutputStream, progress: (Long) -> Unit) {
	val buffer = ByteArray(1024)
	var bytesRead = 0L
	while (true) {
		val readCount = read(buffer)
		if (readCount <= 0) break
		to.write(buffer, 0, readCount)
		bytesRead += readCount
		progress(bytesRead)
	}
	progress(bytesRead)
}

@Composable
fun BigErrorPage(message: String) {
	CenterColumn {
		Row(Modifier.padding(16.dp)) {
			Icon(Icons.Outlined.Error, contentDescription = null)
			Text("Network error!", style = MaterialTheme.typography.headlineMedium)
		}

		Text(message)
	}
}
