package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.settingsScreen

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.pkstudio.localhomeworkandtaskmanager.R
import ru.pkstudio.localhomeworkandtaskmanager.core.components.DefaultTopAppBar
import ru.pkstudio.localhomeworkandtaskmanager.core.components.DeleteDialog
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.LocalHomeworkAndTaskManagerTheme
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.emptyColor
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.filledColor
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.success


@Composable
fun SettingsScreen(
    uiState: SettingsState,
    handleIntent: (SettingsIntent) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            DefaultTopAppBar(
                modifier = Modifier.windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Start
                    )
                ),
                title = uiState.toolbarTitle,
                navigationIcon = Icons.AutoMirrored.Default.ArrowBack,
                navigationAction = {
                    when (uiState.currentScreen) {
                        SettingsViewModel.SETTINGS_MAIN -> {
                            handleIntent(SettingsIntent.NavigateUp)
                        }

                        else -> {
                            handleIntent(SettingsIntent.SetMainScreen)
                        }
                    }

                },
                actions = emptyList()
            )
        }
    ) { paddingValues ->
        when (uiState.currentScreen) {
            SettingsViewModel.SETTINGS_MAIN -> {
                SettingsMainScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    handleIntent = handleIntent
                )
            }

            SettingsViewModel.THEME_SCREEN -> {
                ChangeThemeScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    uiState = uiState,
                    handleIntent = handleIntent
                )
            }

            SettingsViewModel.PASSWORD_CHANGE_SCREEN -> {
                ChangePasswordScreen(
                    uiState = uiState,
                    handleIntent = handleIntent
                )
            }
        }
        if (uiState.isImportAlertDialogOpened) {
            DeleteDialog(
                title = uiState.titleImportAlertDialog,
                comment = uiState.commentImportAlertDialog,
                onConfirm = {
                    handleIntent(SettingsIntent.ImportConfirmed)
                },
                onDismissRequest = {
                    handleIntent(SettingsIntent.CloseImportDialog)
                },
                onDismiss = {
                    handleIntent(SettingsIntent.CloseImportDialog)
                }
            )
        }

        if (uiState.isExportAlertDialogOpened) {
            DeleteDialog(
                title = uiState.titleExportAlertDialog,
                comment = uiState.commentExportAlertDialog,
                onConfirm = {
                    handleIntent(SettingsIntent.ExportConfirmed)
                },
                onDismissRequest = {
                    handleIntent(SettingsIntent.CloseExportDialog)
                },
                onDismiss = {
                    handleIntent(SettingsIntent.CloseExportDialog)
                }
            )
        }

    }
}

@Composable
fun SettingsMainScreen(
    modifier: Modifier = Modifier,
    handleIntent: (SettingsIntent) -> Unit
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            modifier = Modifier.padding(top = 12.dp),
            style = MaterialTheme.typography.titleMedium,
            text = stringResource(R.string.personalization)
        )

        MenuItem(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            onClick = {
                handleIntent(SettingsIntent.OnSetThemeClicked)
            },
            name = stringResource(id = R.string.theme_settings)
        )
        MenuItem(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            onClick = {
                handleIntent(SettingsIntent.OnKanbanSettingsClicked)
            },
            name = stringResource(id = R.string.edit_stages)
        )
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 1.dp,
        )

        Text(
            style = MaterialTheme.typography.titleMedium,
            text = stringResource(R.string.security)
        )

        MenuItem(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            onClick = {
                handleIntent(SettingsIntent.OnChangePasswordClicked)
            },
            name = stringResource(id = R.string.change_password)
        )

        //for future implementation
//        HorizontalDivider(
//            modifier = Modifier.padding(vertical = 8.dp),
//            thickness = 1.dp,
//        )
//
//        Text(
//            style = MaterialTheme.typography.titleMedium,
//            text = stringResource(R.string.data_management)
//        )
//
//        MenuItem(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 8.dp),
//            onClick = {
//                handleIntent(SettingsIntent.OnImportClicked)
//            },
//            name = stringResource(id = R.string.import_database)
//        )
//
//        MenuItem(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 8.dp),
//            onClick = {
//                handleIntent(SettingsIntent.OnExportClicked)
//            },
//            name = stringResource(id = R.string.export_database)
//        )

    }
}

@Composable
private fun ChangeThemeScreen(
    modifier: Modifier = Modifier,
    uiState: SettingsState,
    handleIntent: (SettingsIntent) -> Unit
) {
    BackHandler {
        handleIntent(SettingsIntent.SetMainScreen)
    }
    val isSystemInDarkMode = isSystemInDarkTheme()
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
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
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
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
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
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
    }
}

@Composable
fun ChangePasswordScreen(
    uiState: SettingsState,
    handleIntent: (SettingsIntent) -> Unit
) {
    BackHandler {
        handleIntent(SettingsIntent.SetMainScreen)
    }
    SetPinCode(
        uiState = uiState,
        handleIntent = handleIntent
    )
}

@Composable
private fun MenuItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    name: String
) {
    TextButton(
        modifier = modifier,
        onClick = {
            onClick()
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                style = MaterialTheme.typography.bodyLarge,
                text = name
            )
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowRight,
                contentDescription = ""
            )
        }
    }
}

@Composable
private fun SetPinCode(
    modifier: Modifier = Modifier,
    uiState: SettingsState,
    handleIntent: (SettingsIntent) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.weight(0.8f),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 50.dp),
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                text = uiState.titleText
            )

            PinCode(
                text = uiState.text,
                emptyColor = emptyColor,
                filledColor = filledColor,
                isError = uiState.isError,
                isSuccess = uiState.isSuccess,
            )
        }
        Box(modifier = Modifier.weight(1.2f)) {
            Keyboard(
                modifier = Modifier.padding(top = 50.dp),
                onKeyboardClick = {
                    handleIntent(SettingsIntent.OnKeyboardClicked(it))
                }
            )
        }
    }
}

@Composable
private fun PinCode(
    modifier: Modifier = Modifier,
    text: String,
    filledColor: Color,
    emptyColor: Color,
    isError: Boolean,
    isSuccess: Boolean
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            (1..4).forEach { index ->
                val color by animateColorAsState(
                    targetValue = if (text.length >= index) {
                        if (isError) {
                            MaterialTheme.colorScheme.error
                        } else if (isSuccess) {
                            success
                        } else {
                            filledColor
                        }
                    } else {
                        emptyColor
                    },
                    label = "boxColor"
                )
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(35.dp)
                        .background(color)
                )
            }
        }
        AnimatedVisibility(visible = isError) {
            Text(
                modifier = Modifier.padding(top = 12.dp),
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.error,
                text = stringResource(id = R.string.incorrect_password)
            )
        }
    }

}

@Composable
fun Keyboard(
    modifier: Modifier = Modifier,
    onKeyboardClick: (String) -> Unit
) {

    Column(
        modifier = modifier
    ) {
        listOf(1..3, 4..6, 7..9).forEach { range ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                range.forEach { index ->
                    TextButton(
                        colors = ButtonDefaults.buttonColors().copy(
                            contentColor = MaterialTheme.colorScheme.primary,
                            containerColor = Color.Transparent
                        ),
                        onClick = {
                            Log.d("saaasdsadasd", "Keyboard: ${index.toChar()}")
                            onKeyboardClick(index.toString())
                        }
                    ) {
                        Text(
                            fontSize = 40.sp,
                            text = index.toString()
                        )
                    }

                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                colors = ButtonDefaults.buttonColors().copy(
                    contentColor = MaterialTheme.colorScheme.primary,
                    containerColor = Color.Transparent
                ),
                onClick = {
                    onKeyboardClick("C")
                }
            ) {
                Text(
                    fontSize = 40.sp,
                    text = "C"
                )
            }
            TextButton(
                colors = ButtonDefaults.buttonColors().copy(
                    contentColor = MaterialTheme.colorScheme.primary,
                    containerColor = Color.Transparent
                ),
                onClick = {
                    onKeyboardClick("0")
                }
            ) {
                Text(
                    fontSize = 40.sp,
                    text = "0"
                )
            }
            IconButton(
                colors = IconButtonDefaults.iconButtonColors().copy(
                    contentColor = MaterialTheme.colorScheme.primary,
                    containerColor = Color.Transparent
                ),
                onClick = {
                    onKeyboardClick("-")
                }
            ) {
                Icon(
                    modifier = Modifier.size(40.dp),
                    painter = painterResource(id = R.drawable.icon_backspace),
                    contentDescription = ""
                )
            }

        }
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    LocalHomeworkAndTaskManagerTheme {
        SettingsScreen(
            uiState = SettingsState(
                isSystemTheme = true,
                currentScreen = SettingsViewModel.SETTINGS_MAIN
            ),
            handleIntent = {}
        )
    }
}