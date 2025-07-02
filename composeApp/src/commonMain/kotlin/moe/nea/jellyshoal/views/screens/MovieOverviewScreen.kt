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
import java.net.URL
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

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
							Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
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
								Row {
									Button(onClick = {
										// TODO: move this out of common code
										val fileChooser = JFileChooser()
										fileChooser.dialogTitle = "Download MP4"
										fileChooser.selectedFile = File("${item.item.name}")
										val mp4Filter = FileNameExtensionFilter("MP4", "mp4")
										val mkvFilter = FileNameExtensionFilter("Matroska", "mkv")
										fileChooser.addChoosableFileFilter(mp4Filter)
										fileChooser.addChoosableFileFilter(mkvFilter)
										fileChooser.fileFilter = mp4Filter
										if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
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
											var outputFile = fileChooser.selectedFile
											if (outputFile.extension != containerType)
												outputFile =
													outputFile.resolveSibling(outputFile.nameWithoutExtension + "." + containerType)
											outputFile.parentFile.mkdirs()
											// TODO: show download progress somewhere in sidebar most likely
											URL(downloadUrl).openStream().use { inputStream ->
												outputFile.outputStream().use { outputStream ->
													inputStream.copyTo(outputStream)
												}
											}
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
