package moe.nea.jellyshoal.views.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Traffic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import moe.nea.jellyshoal.data.findPreference
import javax.swing.JFileChooser

@Composable
actual fun ExtraPlatformSettings() {
	TitledBox(
		title = {
			Icon(Icons.Outlined.Traffic, contentDescription = null)
			Text("VLC")
		},
		modifier = Modifier.padding(16.dp),
	) {
		ConfigureFilePath(findPreference { externalVlcPath }, { Text("External VLC Path") })
	}
}

@Composable
fun ConfigureFilePath(
	state: MutableState<String>,
	label: @Composable () -> Unit,
) {
	var configValue by state
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.SpaceBetween,
		modifier = Modifier.padding(16.dp).fillMaxWidth()
	) {
		label()
		OutlinedTextField(
			configValue,
			onValueChange = { value ->
				configValue = value
			},
			trailingIcon = {
				IconButton(onClick = {
					val fileChooser = JFileChooser()
					fileChooser.dialogTitle = "Choose the location of your VLC installation"
					if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
						// TODO: add validation
						state.value = fileChooser.selectedFile.absolutePath
					}
				}) {
					Icon(Icons.Outlined.Folder, contentDescription = "Open File")
				}
			},
			singleLine = true,
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
		)
	}

}

