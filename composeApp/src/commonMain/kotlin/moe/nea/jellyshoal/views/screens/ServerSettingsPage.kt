package moe.nea.jellyshoal.views.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.nea.jellyshoal.data.Account
import moe.nea.jellyshoal.data.findPreference
import moe.nea.jellyshoal.layouts.DefaultSideBar
import moe.nea.jellyshoal.util.ShoalRoute
import moe.nea.jellyshoal.util.findGlobalNavController

class ServerSettingsPage(val server: String) : ShoalRoute {
	@Composable
	override fun Content() {
		var accounts by findPreference { accounts }
		val currentAccountIndex = accounts.indexOfFirst { it.server == server }
		require(currentAccountIndex != -1) { "Account not found: $server" }
		val currentAccount = accounts[currentAccountIndex]
		fun setAccount(account: Account) {
			accounts = accounts.toMutableList().apply {
				this[currentAccountIndex] = account
			}
		}

		val serverName = remember { mutableStateOf(currentAccount.name ?: "") }

		val nav = findGlobalNavController()
		DefaultSideBar {
			Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
				IconButton(onClick = {
					nav.goBack()
				}) {
					Icon(
						Icons.AutoMirrored.Outlined.ArrowBack,
						contentDescription = "Go Back"
					)
				}
				Text(
					"Settings for " + currentAccount.userFriendlyName(),
					style = MaterialTheme.typography.headlineMedium
				)
				Spacer(Modifier.weight(1f))
				Button(
					onClick = {
						setAccount(
							currentAccount.copy(
								name = serverName.value.trim().takeIf { it.isNotEmpty() }
							))
						nav.goBack()
					}) {
					Text("Save", style = MaterialTheme.typography.bodyMedium)
				}
			}
			Column(
				modifier = Modifier.padding(16.dp),
				verticalArrangement = Arrangement.spacedBy(16.dp)
			) {
				TitledBox(title = { Text("Connection Settings") }) {
					Column {
						ConfigurableOptionRow(
							{
								Icon(Icons.Outlined.Link, contentDescription = null)
								Text("Server Address")
							},
							{
								OutlinedTextField(
									currentAccount.server,
									onValueChange = { it: String -> },
									enabled = false,
								)
							})
						ConfigureText(serverName, {
							Icon(Icons.AutoMirrored.Outlined.Label, contentDescription = null)
							Text("Nickname")
						})
					}
				}
			}
		}
	}
}
