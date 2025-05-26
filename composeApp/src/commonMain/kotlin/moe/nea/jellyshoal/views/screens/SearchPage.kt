package moe.nea.jellyshoal.views.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.launch
import moe.nea.jellyshoal.data.findPreference
import moe.nea.jellyshoal.layouts.DefaultSideBar
import moe.nea.jellyshoal.util.ShoalRoute
import moe.nea.jellyshoal.util.jellyfin.ItemWithProvenance
import org.jellyfin.sdk.api.operations.ItemsApi
import org.jellyfin.sdk.model.api.BaseItemKind
import org.jellyfin.sdk.model.api.ImageType

object SearchPage : ShoalRoute {
	val logger = KotlinLogging.logger { }

	@Composable
	override fun Content() {
		val (search, setSearch) = remember { mutableStateOf(TextFieldValue("")) }
		val (accounts) = findPreference { accounts }
		val (results, setResults) = remember { mutableStateOf<List<ItemWithProvenance>>(emptyList()) }
		val scope = rememberCoroutineScope()

		// TODO. add SeachApi for search recommendations in the background (debounce jb compose?)
		fun updateSearch() {
			scope.launch { // TODO: interruption?
				val searchText = search.text

				val allItems = mutableListOf<ItemWithProvenance>()

				accounts.forEach { account ->
					val api = ItemsApi(account.createApiClient())
					val items = api.getItems(
						searchTerm = searchText,
						includeItemTypes = setOf(
							BaseItemKind.MOVIE, BaseItemKind.SERIES,
							BaseItemKind.EPISODE, BaseItemKind.SEASON,
						),
						recursive = true
					)
					// TODO: pagination? auto scrolling?
					items.content.items.mapTo(allItems) {
						ItemWithProvenance(account, it)
					}
				}
				setResults(allItems)
			}
		}

		DefaultSideBar {
			Column(modifier = Modifier.fillMaxSize()) {
				OutlinedTextField(
					value = search,
					onValueChange = { setSearch(it) },
					modifier = Modifier.padding(all = 16.dp).fillMaxWidth(),
					singleLine = true,
					leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = "Search") },
					keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
					keyboardActions = KeyboardActions(onDone = {
						updateSearch()
					}),
				)
				LazyColumn {
					items(results) { item ->
						val primaryImageUrl = item.getImage(ImageType.PRIMARY)

						Card(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
							Row(modifier = Modifier.padding(16.dp)) {
								if (primaryImageUrl != null) {
									AsyncImage(
										primaryImageUrl,
										modifier = Modifier.height(210.dp),
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
				}
			}
		}
	}
}
