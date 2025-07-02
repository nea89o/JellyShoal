@file:OptIn(ExperimentalTime::class)

package moe.nea.jellyshoal.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

object DownloadManager {
	private val _downloads = mutableStateMapOf<DownloadRef, DownloadItem>()

	@get:Composable
	val downloads: List<DownloadItem> get() = _downloads.values.filter { !it.cancelled }.sortedBy { it.label }
	fun create(label: String): DownloadRef {
		val ref = DownloadRef(UUID.randomUUID())
		_downloads[ref] = DownloadItem(label, 0F, null, false, ref)
		return ref
	}

	private fun mutate(ref: DownloadRef, modify: (DownloadItem) -> DownloadItem) {
		val before = _downloads[ref] ?: return
		_downloads[ref] = modify(before)
	}

	fun updateProgress(ref: DownloadRef, progress: Float) {
		mutate(ref) { it.copy(progress = progress) }
	}

	fun finish(ref: DownloadRef) {
		mutate(ref) { it.copy(finishedAt = Clock.System.now()) }
	}

	fun markDone(ref: DownloadRef) {
		_downloads.remove(ref)
	}

	fun markCancelled(ref: DownloadRef) {
		mutate(ref) { it.copy(cancelled = true) }
	}

	fun isCancelled(ref: DownloadRef): Boolean {
		return _downloads[ref]?.cancelled ?: true
	}

	data class DownloadRef(
		val id: UUID,
	)
}

@Composable
fun DownloadList() {
	val items = DownloadManager.downloads
	if (items.isEmpty()) return
	// TODO: should this just be a "Downloads" tab?
	Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
		Text(text = "Downloads", style = MaterialTheme.typography.titleLarge)
		items.forEach { downloadItem ->
			Row {
				Column(modifier = Modifier.weight(1f)) {
					Text(text = downloadItem.label)
					if (downloadItem.finishedAt == null)
						LinearProgressIndicator(
							modifier = Modifier.fillMaxWidth(),
							progress = { downloadItem.progress })
				}
				if (downloadItem.finishedAt == null) {
					IconButton(onClick = {
						DownloadManager.markCancelled(downloadItem.ref)
					}) {
						Icon(Icons.Outlined.Cancel, contentDescription = "Cancel")
					}
				} else {
					IconButton(onClick = {
						DownloadManager.markDone(downloadItem.ref)
					}) {
						Icon(Icons.Outlined.CheckCircle, contentDescription = "Done")
					}
				}
			}
		}
	}
}

data class DownloadItem(
	// TODO: should this be a bunch of MutableStates instead?
	val label: String,
	val progress: Float,
	val finishedAt: Instant?,
	val cancelled: Boolean,
	val ref: DownloadManager.DownloadRef,
)
