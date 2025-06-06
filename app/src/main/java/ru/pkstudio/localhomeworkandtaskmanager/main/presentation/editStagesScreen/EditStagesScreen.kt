package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.editStagesScreen

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.pkstudio.localhomeworkandtaskmanager.R
import ru.pkstudio.localhomeworkandtaskmanager.core.components.DefaultTopAppBar
import ru.pkstudio.localhomeworkandtaskmanager.core.components.DeleteDialog
import ru.pkstudio.localhomeworkandtaskmanager.core.components.StageColorPaletteDialog
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.editStagesScreen.components.EditStageCard
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.LocalHomeworkAndTaskManagerTheme


@Composable
fun EditStagesScreen(
    uiState: EditStagesState,
    handleIntent: (EditStagesIntent) -> Unit,
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
                title = stringResource(id = R.string.edit_stages),
                navigationIcon = Icons.AutoMirrored.Default.ArrowBack,
                navigationAction = {
                    handleIntent(EditStagesIntent.NavigateUp)
                },
                actions = emptyList()
            )
        }
    ) { paddingValues ->
        if (uiState.isColorAlertDialogOpened){
            StageColorPaletteDialog(
                colorList = uiState.colorList,
                onSelectClick = {
                    handleIntent(EditStagesIntent.ConfirmColorChange(color = it))
                },
                onBtnDismissClick = {
                    handleIntent(EditStagesIntent.CloseColorPickerDialog)
                },
                onDismiss = {
                    handleIntent(EditStagesIntent.CloseColorPickerDialog)
                }
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            itemsIndexed(
                items = uiState.stagesList,
                key = { _, item ->
                    item.id!!
                }
            ) { index, model ->
                EditStageCard(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    stage = model,
                    onTextChanged = { text ->
                        handleIntent(
                            EditStagesIntent.OnStageNameChange(
                                index = index,
                                name = text
                            )
                        )
                    },
                    onAddBtnClick = {
                        handleIntent(EditStagesIntent.OnAddStageBtmClick(index))
                    },
                    onDeleteBtnClick = {
                        handleIntent(EditStagesIntent.OnDeleteStageBtmClick(index = index))
                    },
                    onColorPaletteClick = {
                        handleIntent(EditStagesIntent.OnColorPaletteClicked(index))
                    }
                )
            }
        }

        if (uiState.isDeleteAlertDialogOpened) {
            DeleteDialog(
                title = uiState.titleDeleteAlertDialog,
                comment = uiState.commentDeleteAlertDialog,
                onConfirm = {
                    handleIntent(EditStagesIntent.ConfirmDeleteStage)
                },
                onDismissRequest = {
                    handleIntent(EditStagesIntent.CloseDeleteDialog)
                },
                onDismiss = {
                    handleIntent(EditStagesIntent.CloseDeleteDialog)
                }
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    LocalHomeworkAndTaskManagerTheme {
        EditStagesScreen(
            uiState = EditStagesState(),
            handleIntent = {}
        )
    }
}