package moe.nea.jellyshoal.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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

	Card(modifier = modifier.padding(8.dp).height(260.dp)) {
		Row(modifier = Modifier.padding(16.dp).fillMaxHeight()) {
			if (primaryImageUrl != null) {
				AsyncImage(
					primaryImageUrl,
					modifier = Modifier.height(210.dp).align(Alignment.CenterVertically),
					contentDescription = "The primary image poster for ${item.item.name}",
				)
			}
			Column(modifier = Modifier.padding(horizontal = 16.dp)) {
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

}

