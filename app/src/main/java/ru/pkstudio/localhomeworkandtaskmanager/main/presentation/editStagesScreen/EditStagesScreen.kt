package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.editStagesScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.editStagesScreen.components.EditStageCard
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.LocalHomeworkAndTaskManagerTheme


@Composable
fun EditStagesScreen(
    uiState: EditStagesState,
    handleIntent: (EditStagesIntent) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            DefaultTopAppBar(
                title = stringResource(id = R.string.edit_stages),
                navigationIcon = Icons.AutoMirrored.Default.ArrowBack,
                navigationAction = {
                    handleIntent(EditStagesIntent.NavigateUp)
                },
                actions = emptyList()
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            itemsIndexed(uiState.stagesList) { index, model ->
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