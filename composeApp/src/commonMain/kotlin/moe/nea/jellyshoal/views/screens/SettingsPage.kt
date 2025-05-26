package moe.nea.jellyshoal.views.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.oshai.kotlinlogging.KotlinLogging
import moe.nea.jellyshoal.data.SelectedColorTheme
import moe.nea.jellyshoal.data.findPreference
import moe.nea.jellyshoal.layouts.DefaultSideBar
import moe.nea.jellyshoal.util.ShoalRoute
import moe.nea.jellyshoal.util.findGlobalNavController

@Composable
fun TitledBox(
	title: @Composable () -> Unit,
	modifier: Modifier = Modifier,
	borderColor: Color = MaterialTheme.colorScheme.primaryContainer,
	content: @Composable () -> Unit,
) {

	Box(
		modifier = modifier
			.border(
				2.dp, borderColor,
				RoundedCornerShape(
					12.dp
				)
			)
	) {
		Column {
			Surface(
				modifier = Modifier
					.fillMaxWidth()
					.clip(
						RoundedCornerShape(
							topStart = 12.dp,
							topEnd = 12.dp,
						)
					),
				color = borderColor,
			) {
				Row(
					modifier = Modifier.padding(16.dp),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(12.dp)
				) {
					title()
				}
			}

			Box {
				content()
			}
		}
	}

}

object SettingsPage : ShoalRoute {
	val logger = KotlinLogging.logger { }

	@Composable
	override fun Content() {
		var accounts by findPreference { accounts }
		val nav = findGlobalNavController()
		DefaultSideBar {
			Column {
				TitledBox(
					title = { Text("Account") },
					modifier = Modifier.padding(16.dp),
				) {
					LazyColumn {
						items(accounts) { account ->
							Row(
								horizontalArrangement = Arrangement.SpaceBetween,
								modifier = Modifier.fillMaxWidth().padding(16.dp),
								verticalAlignment = Alignment.CenterVertically
							) {
								Text(account.server)
								IconButton(onClick = {
									val newAccounts = accounts.filter { it != account }
									logger.info { "Trimming accounts down to $newAccounts" }
									accounts = newAccounts
								}) {
									Icon(Icons.Outlined.Delete, contentDescription = "Remove Account")
								}
							}
						}
						item {
							Button(onClick = {
								nav.navigate(SelectServerPage)
							}, modifier = Modifier.padding(16.dp)) {
								Icon(Icons.Outlined.Add, contentDescription = null)
								Text("Add Account")
							}
						}
					}
				}
				TitledBox(
					title = { Text("Colors") },
					modifier = Modifier.padding(16.dp),
				) {
					var colorTheme by findPreference { colorTheme }

					@Composable
					fun theme(theme: SelectedColorTheme) {
						Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
							RadioButton(theme == colorTheme, onClick = { colorTheme = theme })
							Text(theme.userFriendlyName)
						}
					}

					Column {
						theme(SelectedColorTheme.LIGHT)
						theme(SelectedColorTheme.DARK)
						theme(SelectedColorTheme.SYSTEM)
					}
				}
			}
		}
	}
}
