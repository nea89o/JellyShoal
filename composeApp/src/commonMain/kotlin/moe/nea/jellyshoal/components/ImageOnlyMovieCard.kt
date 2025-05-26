package moe.nea.jellyshoal.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import moe.nea.jellyshoal.util.jellyfin.ItemWithProvenance
import org.jellyfin.sdk.model.api.BaseItemKind
import org.jellyfin.sdk.model.api.ImageType


@Composable
fun ImageOnlyMovieCard(
	item: ItemWithProvenance,
	modifier: Modifier = Modifier
) {
	val progress = item.getWatchProgress()
	val imgHeight = 300.dp

	Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier.padding(16.dp)) {
		SubcomposeAsyncImage(
			item.getImage(ImageType.PRIMARY),
			modifier = Modifier.height(imgHeight),
			contentDescription = item.item.name,
		) {
			val state by painter.state.collectAsState()
			if (state is AsyncImagePainter.State.Success) {
				val imageSize = state.painter!!.intrinsicSize
				val imgWidth = imageSize.width * imgHeight / imageSize.height
				Box {
					this@SubcomposeAsyncImage.SubcomposeAsyncImageContent()
					Box(modifier = Modifier.width(imgWidth).fillMaxHeight()) {
						Column(
							modifier = Modifier.padding(16.dp).fillMaxHeight(),
							verticalArrangement = Arrangement.Bottom,
							horizontalAlignment = Alignment.CenterHorizontally,
						) {
							if (progress == null) {

							} else {
								LinearProgressIndicator(
									progress = { progress.progress },
									modifier = Modifier.fillMaxWidth(),
									drawStopIndicator = {}
								)
							}
						}
					}
				}
			} else {
				CircularProgressIndicator()
			}
		}
		val seasonName = item.item.seasonName
		if (item.item.type == BaseItemKind.EPISODE && seasonName != null) {
			Text(seasonName, style = MaterialTheme.typography.titleMedium)
			Text(item.item.name!!, style = MaterialTheme.typography.bodyMedium)
		} else {
			Text(item.item.name!!, style = MaterialTheme.typography.titleMedium)
			Text(
				item.item.productionYear.toString(),
				style = MaterialTheme.typography.bodyMedium,
			)

		}
	}
}
