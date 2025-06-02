package moe.nea.jellyshoal.views.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.launch
import moe.nea.jellyshoal.components.ImageOnlyMovieCard
import moe.nea.jellyshoal.components.MovieCard
import moe.nea.jellyshoal.data.findPreference
import moe.nea.jellyshoal.layouts.DefaultSideBar
import moe.nea.jellyshoal.util.ShoalRoute
import moe.nea.jellyshoal.util.jellyfin.ItemWithProvenance
import org.jellyfin.sdk.api.client.extensions.itemsApi
import org.jellyfin.sdk.model.api.BaseItemKind

object SearchPage : ShoalRoute {
	val logger = KotlinLogging.logger { }

	@Composable
	override fun Content() {
		val (search, setSearch) = remember { mutableStateOf(TextFieldValue("")) }
		val (accounts) = findPreference { accounts }
		val (results, setResults) = remember { mutableStateOf<List<ItemWithProvenance>>(emptyList()) }
		val errors = remember { mutableStateListOf<String>() }
		val scope = rememberCoroutineScope()

		// TODO. add SeachApi for search recommendations in the background (debounce jb compose?)
		fun updateSearch() {
			scope.launch { // TODO: interruption?
				val searchText = search.text

				val allItems = mutableListOf<ItemWithProvenance>()

				accounts.forEach { account ->
					val result = account.useApiClient {
						it.itemsApi.getItems(
							searchTerm = searchText,
							includeItemTypes = setOf(
								BaseItemKind.MOVIE, BaseItemKind.SERIES,
								BaseItemKind.EPISODE, BaseItemKind.SEASON,
							),
							recursive = true
						)
					}
					result.handle(
						{
							it.content.items.mapTo(allItems) {
								ItemWithProvenance(account, it)
							}
						},
						{
							errors.add(it)
						}
					)
					// TODO: pagination? auto scrolling?

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
				val useCompactCards = findPreference { movieCardStyle }.value.useCompactInGeneral
				LazyColumn {
					items(errors) { error ->
						Text(error, color = MaterialTheme.colorScheme.error)
					}
					items(results) { item ->
						if (useCompactCards) {
							ImageOnlyMovieCard(item)
						} else {
							MovieCard(item, modifier = Modifier.fillMaxWidth())
						}
					}
				}
			}
		}
	}
}
