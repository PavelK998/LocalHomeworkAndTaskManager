package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.settingsScreen

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import ru.pakarpichev.homeworktool.core.presentation.components.DefaultTopAppBar
import ru.pkstudio.localhomeworkandtaskmanager.R
import ru.pkstudio.localhomeworkandtaskmanager.core.components.DefaultButton
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.LocalHomeworkAndTaskManagerTheme


@Composable
fun SettingsScreen(
    uiState: SettingsState,
    handleIntent: (SettingsIntent) -> Unit,
) {
    val isSystemInDarkMode = isSystemInDarkTheme()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            DefaultTopAppBar(
                title = stringResource(id = R.string.settings),
                navigationIcon = Icons.AutoMirrored.Default.ArrowBack,
                navigationAction = {
                    handleIntent(SettingsIntent.NavigateUp)
                },
                actions = emptyList()
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge,
                    text = stringResource(id = R.string.enable_system_theme)
                )
                Switch(
                    checked = uiState.isSystemTheme,
                    onCheckedChange = {
                        handleIntent(SettingsIntent.SetSystemTheme(isSystemInDarkMode))
                    },
                    colors = SwitchDefaults.colors().copy(
                        checkedTrackColor = MaterialTheme.colorScheme.onSecondary,
                        checkedThumbColor = MaterialTheme.colorScheme.tertiaryContainer,
                        uncheckedThumbColor = MaterialTheme.colorScheme.primary,
                        uncheckedTrackColor = MaterialTheme.colorScheme.background
                    )
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge,
                    text = stringResource(id = R.string.enable_light_theme)
                )
                Switch(
                    enabled = uiState.isLightThemeBtnEnabled,
                    checked = uiState.isLightTheme,
                    onCheckedChange = {
                        handleIntent(SettingsIntent.SetLightTheme)
                    },
                    colors = SwitchDefaults.colors().copy(
                        checkedTrackColor = MaterialTheme.colorScheme.onSecondary,
                        checkedThumbColor = MaterialTheme.colorScheme.tertiaryContainer,
                        uncheckedThumbColor = MaterialTheme.colorScheme.primary,
                        uncheckedTrackColor = MaterialTheme.colorScheme.background
                    )
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    text = stringResource(id = R.string.enable_dark_theme)
                )
                Switch(
                    enabled = uiState.isDarkThemeBtnEnabled,
                    checked = uiState.isDarkTheme,
                    onCheckedChange = {
                        handleIntent(SettingsIntent.SetDarkTheme)
                    },
                    colors = SwitchDefaults.colors().copy(
                        checkedTrackColor = MaterialTheme.colorScheme.onSecondary,
                        checkedThumbColor = MaterialTheme.colorScheme.tertiaryContainer,
                        uncheckedThumbColor = MaterialTheme.colorScheme.primary,
                        uncheckedTrackColor = MaterialTheme.colorScheme.background
                    )
                )
            }
            if (uiState.isDynamicColorAvailable) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        text = stringResource(id = R.string.enable_dynamic_color_theme)
                    )
                    Switch(
                        checked = uiState.isDynamicColor,
                        onCheckedChange = {
                            handleIntent(SettingsIntent.SetDynamicColors)
                        },
                        colors = SwitchDefaults.colors().copy(
                            checkedTrackColor = MaterialTheme.colorScheme.onSecondary,
                            checkedThumbColor = MaterialTheme.colorScheme.tertiaryContainer,
                            uncheckedThumbColor = MaterialTheme.colorScheme.primary,
                            uncheckedTrackColor = MaterialTheme.colorScheme.background
                        )
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                thickness = 2.dp,
            )

            DefaultButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onClick = {
                    handleIntent(SettingsIntent.OnEditStagesClicked)
                },
                textStyle = MaterialTheme.typography.headlineSmall,
                text = stringResource(id = R.string.edit_stages)
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    LocalHomeworkAndTaskManagerTheme {
        SettingsScreen(
            uiState = SettingsState(
                isSystemTheme = true
            ),
            handleIntent = {}
        )
    }
}