package moe.nea.jellyshoal.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import moe.nea.jellyshoal.util.jellyfin.ItemWithProvenance
import org.jellyfin.sdk.model.api.ImageType

@Composable
fun MovieCard(
	item: ItemWithProvenance,
	modifier: Modifier = Modifier
) {
	val primaryImageUrl = item.getImage(ImageType.PRIMARY)

	val cardHeight = 260.dp
	val padding = 16.dp


	val progress = item.getWatchProgress()

	val progressHeight = 24.dp

	Card(modifier = modifier.padding(8.dp).height(cardHeight)) {
		Column(
			verticalArrangement = Arrangement.SpaceBetween,
			modifier = Modifier.fillMaxHeight()
		) {
			Column(modifier = Modifier.height(cardHeight - (if (progress == null) 0.dp else padding * 2 + progressHeight))) {
				Row {
					if (primaryImageUrl != null) {
						AsyncImage(
							primaryImageUrl,
							modifier = Modifier.fillMaxHeight()
								.align(Alignment.CenterVertically),
							contentDescription = "The primary image poster for ${item.item.name}",
						)
					}
					Column(modifier = Modifier.padding(16.dp)) {
						Text(
							text = item.item.name ?: "<missing name>",
							style = MaterialTheme.typography.titleLarge
						)
						Text(
							text = item.item.overview ?: "<missing overview>",
							style = MaterialTheme.typography.bodyLarge
						)
					}
				}
			}
			if (progress != null) {
				Surface(
					color = MaterialTheme.colorScheme.surfaceVariant
				) {
					Row(
						verticalAlignment = Alignment.CenterVertically,
						modifier =
							Modifier.padding(padding)
								.height(progressHeight)
					) {
						Text(progress.current.format())
						LinearProgressIndicator(
							{ progress.progress },
							modifier = Modifier.weight(1F)
								.padding(horizontal = 8.dp)
						)
						Text(progress.total.format())
					}
				}
			}
		}
	}
}

