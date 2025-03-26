package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.settingsScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
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

@Preview
@Composable
private fun Preview() {
    LocalHomeworkAndTaskManagerTheme {
        SettingsScreen(
            uiState = SettingsState(),
            handleIntent = {}
        )
    }
}