package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkInfo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.pkstudio.localhomeworkandtaskmanager.R
import ru.pkstudio.localhomeworkandtaskmanager.core.components.DefaultTopAppBar
import ru.pkstudio.localhomeworkandtaskmanager.core.components.DeleteDialog
import ru.pkstudio.localhomeworkandtaskmanager.core.components.Loading
import ru.pkstudio.localhomeworkandtaskmanager.core.components.TopAppBarAction
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkInfo.components.MenuItemCard
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.LocalHomeworkAndTaskManagerTheme


@Composable
fun HomeworkInfoScreen(
    uiState: HomeworkInfoState,
    handleIntent: (HomeworkInfoIntent) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (!uiState.isEditMode) {
                InfoTopBar(
                    isMenuOpened = uiState.isSettingsMenuOpened,
                    navigateUp = { handleIntent(HomeworkInfoIntent.NavigateUp) },
                    onSettingsClicked = { handleIntent(HomeworkInfoIntent.OnSettingsClicked) },
                    closeSettingsMenu = { handleIntent(HomeworkInfoIntent.CloseSettingsMenu) },
                    onEditClick = { handleIntent(HomeworkInfoIntent.OnEditClick) },
                    onDeleteBtnClick = { handleIntent(HomeworkInfoIntent.OnDeleteBtnClick) }
                )
            } else {
                EditTopBar(
                    saveResult = {
                        handleIntent(HomeworkInfoIntent.ConfirmEditResult)
                    },
                    dismissEditMode = {
                        handleIntent(HomeworkInfoIntent.DismissEditMode)
                    }
                )
            }

        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Loading()
        } else {
            if (uiState.isDeleteDialogOpened) {
                DeleteDialog(
                    title = uiState.deleteDialogTitle,
                    comment = uiState.deleteDialogDescription,
                    onDismiss = {
                        handleIntent(HomeworkInfoIntent.CloseDeleteAlertDialog)
                    },
                    onConfirm = {
                        handleIntent(HomeworkInfoIntent.DeleteConfirm)
                        handleIntent(HomeworkInfoIntent.CloseDeleteAlertDialog)
                    }
                )
            }
            var menuWidth by remember {
                mutableStateOf(0.dp)
            }
            val density = LocalDensity.current
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(top = 12.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        text = uiState.subjectNameText
                    )
                    Text(
                        modifier = Modifier
                            .padding(top = 8.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        text = uiState.addDateText
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        Box {
                            MenuItemCard(
                                modifier = Modifier
                                    .onGloballyPositioned { layoutCoordinates ->
                                        menuWidth = with(density) {
                                            layoutCoordinates.size.width.toDp()
                                        }
                                    },
                                stageName = uiState.currentSelectStageName,
                                onCLick = {
                                    handleIntent(HomeworkInfoIntent.OnStageSelectClick)
                                },
                                isActive = uiState.isStageMenuOpened
                            )
                            DropdownMenu(
                                modifier = Modifier
                                    .width(menuWidth)
                                    .heightIn(
                                        min = Dp.Unspecified,
                                        max = 250.dp
                                    ),
                                expanded = uiState.isStageMenuOpened,
                                onDismissRequest = {
                                    handleIntent(HomeworkInfoIntent.CloseStageMenu)
                                },
                            ) {
                                uiState.stagesList.forEachIndexed { index, stage ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                modifier = Modifier.fillMaxWidth(),
                                                text = stage.stageName,
                                                style = MaterialTheme.typography.bodyMedium,
                                                textAlign = TextAlign.Center
                                            )
                                        },
                                        onClick = {
                                            HomeworkInfoIntent.OnMenuItemClick(
                                                index = index,
                                                stageId = stage.id ?: 0L
                                            )
                                            handleIntent(HomeworkInfoIntent.CloseStageMenu)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                when (uiState.isEditMode) {
                    true -> {
                        EditMode(
                            modifier = Modifier.padding(top = 16.dp),
                            homeworkName = uiState.homeworkEditName,
                            homeworkDescription = uiState.homeworkEditDescription,
                            onHomeworkNameChange = {
                                handleIntent(HomeworkInfoIntent.OnHomeworkEditNameChange(it))
                            },
                            onHomeworkDescriptionChange = {
                                handleIntent(HomeworkInfoIntent.OnHomeworkEditDescriptionChange(it))
                            }
                        )
                    }

                    false -> {
                        InfoMode(
                            modifier = Modifier.padding(top = 16.dp).padding(horizontal = 16.dp),
                            homeworkName = uiState.homeworkUiModel?.name ?: "",
                            homeworkDescription = uiState.homeworkUiModel?.description ?: ""
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoTopBar(
    modifier: Modifier = Modifier,
    isMenuOpened: Boolean,
    navigateUp: () -> Unit,
    onSettingsClicked: () -> Unit,
    closeSettingsMenu: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteBtnClick: () -> Unit,
) {
    DefaultTopAppBar(
        modifier = modifier,
        title = "",
        navigationIcon = Icons.AutoMirrored.Default.ArrowBack,
        navigationAction = {
            navigateUp()
        },
        actions = listOf(
            TopAppBarAction(
                image = Icons.Default.Settings,
                contentDescription = "",
                action = {
                    onSettingsClicked()
                },
                tint = MaterialTheme.colorScheme.onBackground
            )
        ),
        dropDownMenu = {
            DropdownMenu(
                expanded = isMenuOpened,
                onDismissRequest = {
                    closeSettingsMenu()
                }
            ) {
                DropdownMenuItem(
                    text = {
                        Text(text = stringResource(id = R.string.edit))
                    },
                    trailingIcon = {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "")
                    },
                    onClick = {
                        onEditClick()
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text(text = stringResource(id = R.string.delete))
                    },
                    trailingIcon = {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "")
                    },
                    onClick = {
                        onDeleteBtnClick()
                    }
                )
            }
        }
    )
}

@Composable
private fun EditTopBar(
    modifier: Modifier = Modifier,
    dismissEditMode: () -> Unit,
    saveResult: () -> Unit,
) {
    DefaultTopAppBar(
        modifier = modifier,
        title = stringResource(id = R.string.edit),
        navigationIcon = Icons.AutoMirrored.Default.ArrowBack,
        navigationAction = {
            dismissEditMode()
        },
        actions = listOf(
            TopAppBarAction(
                image = Icons.Default.Done,
                contentDescription = "",
                action = {
                    saveResult()
                },
                tint = MaterialTheme.colorScheme.onBackground
            )
        )
    )
}

@Composable
private fun InfoMode(
    modifier: Modifier = Modifier,
    homeworkName: String,
    homeworkDescription: String,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            style = MaterialTheme.typography.headlineMedium,
            text = homeworkName
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            text = homeworkDescription
        )
    }
}

@Composable
private fun EditMode(
    modifier: Modifier = Modifier,
    homeworkName: String,
    homeworkDescription: String,
    onHomeworkNameChange: (String) -> Unit,
    onHomeworkDescriptionChange: (String) -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        TextField(
            textStyle = MaterialTheme.typography.bodyLarge,
            colors = TextFieldDefaults.colors().copy(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
            ),
            value = homeworkName,
            onValueChange = {
                onHomeworkNameChange(it)
            },
            label = {
                Text(text = stringResource(id = R.string.add_homework_title_label))
            }
        )
        TextField(
            textStyle = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(top = 8.dp),
            value = homeworkDescription,
            colors = TextFieldDefaults.colors().copy(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
            ),
            onValueChange = {
                onHomeworkDescriptionChange(it)
            },
            label = {
                Text(text = stringResource(id = R.string.add_homework_description_label))
            }
        )
    }
}

@Preview
@Composable
private fun Preview() {
    LocalHomeworkAndTaskManagerTheme {
        HomeworkInfoScreen(
            uiState = HomeworkInfoState(),
            handleIntent = {}
        )
    }
}