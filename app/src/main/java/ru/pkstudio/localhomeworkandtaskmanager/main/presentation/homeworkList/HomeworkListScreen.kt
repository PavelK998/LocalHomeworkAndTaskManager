package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.pkstudio.localhomeworkandtaskmanager.R
import ru.pkstudio.localhomeworkandtaskmanager.core.components.DefaultFloatingActionButton
import ru.pkstudio.localhomeworkandtaskmanager.core.components.DefaultTopAppBar
import ru.pkstudio.localhomeworkandtaskmanager.core.components.DeleteDialog
import ru.pkstudio.localhomeworkandtaskmanager.core.components.EmptyScreen
import ru.pkstudio.localhomeworkandtaskmanager.core.components.GradientFloatingActionButton
import ru.pkstudio.localhomeworkandtaskmanager.core.components.Loading
import ru.pkstudio.localhomeworkandtaskmanager.core.components.TopAppBarAction
import ru.pkstudio.localhomeworkandtaskmanager.core.components.kanban.KanbanBoard
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList.components.HomeworkCard
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList.components.KanbanColumnFiller
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList.components.KanbanHeader
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.LocalHomeworkAndTaskManagerTheme

@Composable
fun HomeworkListScreen(
    uiState: HomeworkListState,
    handleIntent: (HomeworkListIntent) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (uiState.isEditModeEnabled) {
                DefaultTopAppBar(
                    title = uiState.numberOfCheckedCards.toString(),
                    navigationIcon = Icons.Default.Close,
                    navigationAction = {
                        handleIntent.invoke(HomeworkListIntent.TurnEditMode(index = -1))
                    },
                    actions = listOf(
                        TopAppBarAction(
                            image = Icons.Default.Delete,
                            contentDescription = "Delete",
                            action = {
                                handleIntent.invoke(HomeworkListIntent.DeleteCards)
                            },
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    )
                )
            } else {
                DefaultTopAppBar(
                    title = uiState.subjectName,
                    navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                    navigationAction = {
                        handleIntent.invoke(HomeworkListIntent.NavigateUp)
                    },
                    dropDownMenu = {
                        DropdownMenu(
                            expanded = uiState.isDropDownMenuVisible,
                            onDismissRequest = {
                                handleIntent(HomeworkListIntent.ShrinkMenu)
                            }
                        ) {
                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "edit"
                                    )
                                },
                                text = {
                                    Text(
                                        text = stringResource(id = R.string.edit)
                                    )
                                },
                                onClick = {

                                }
                            )
                        }
                    },
                    actions = if (uiState.isKanbanScreenVisible) {
                        listOf(
                            TopAppBarAction(
                                image = Icons.Default.Settings,
                                action = {
                                    handleIntent(HomeworkListIntent.NavigateToEditStages)
                                },
                                contentDescription = "edit stages",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    } else {
                        emptyList()
                    }
                )
            }
        },
        floatingActionButton = {
            if (uiState.isScreenEmpty) {
                GradientFloatingActionButton(
                    onClick = {
                        handleIntent.invoke(HomeworkListIntent.NavigateToAddHomework)
                    },
                    imageVector = Icons.Default.Add,
                )
            } else {
                AnimatedVisibility(
                    visible = uiState.isFABVisible,
                    enter = slideInHorizontally() + fadeIn(),
                    exit = slideOutHorizontally() + fadeOut()
                ) {
                    DefaultFloatingActionButton(
                        onClick = {
                            handleIntent.invoke(HomeworkListIntent.NavigateToAddHomework)
                        },
                        imageVector = Icons.Default.Add
                    )
                }
            }
        }
    ) { paddingValues ->
        if (uiState.isDeleteDialogOpen) {
            DeleteDialog(
                title = uiState.deleteDialogTitle,
                comment = uiState.deleteDialogDescription,
                onConfirm = {
                    handleIntent(HomeworkListIntent.ConfirmDeleteCards)
                },
                onDismissRequest = {
                    handleIntent(HomeworkListIntent.CloseDeleteAlertDialog)
                },
                onDismiss = {
                    handleIntent(HomeworkListIntent.CloseDeleteAlertDialog)
                }
            )
        }
        if (uiState.isLoading) {
            Loading()
        } else if (uiState.isScreenEmpty) {
            EmptyScreen(modifier = Modifier.padding(paddingValues))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier
                            .padding(horizontal = 8.dp),
                    ) {
                        uiState.segmentedButtonOptions.forEachIndexed { index, element ->
                            SegmentedButton(
                                colors = SegmentedButtonDefaults.colors().copy(
                                    activeContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    activeContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    activeBorderColor = MaterialTheme.colorScheme.outline
                                ),
                                selected = uiState.segmentedButtonSelectedIndex == index,
                                onClick = {
                                    handleIntent(HomeworkListIntent.OnSegmentedButtonClick(index))
                                },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = uiState.segmentedButtonOptions.size
                                )
                            ) {
                                Text(text = element)
                            }
                        }
                    }
                }
                HorizontalDivider(
                    modifier = Modifier.padding(8.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline
                )
                if (uiState.isKanbanScreenVisible) {
                    KanbanBoard(
                        modifier = Modifier.fillMaxSize(),
                        items = uiState.kanbanItemsList,
                        header = {
                            KanbanHeader(
                                modifier = Modifier.fillMaxWidth(),
                                model = it
                            )
                        },
                        footer = {
                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.primaryContainer
                            )
                        },
                        columnFiller = {
                            KanbanColumnFiller(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                model = it
                            )
                        },
                        columnWidth = 240.dp,
                        columnBackgroundColor = Color.Transparent,
                        borderColor = MaterialTheme.colorScheme.primaryContainer,
                        onColumnFillerClicked = { rowIndex, columnIndex ->
                            handleIntent(
                                HomeworkListIntent.NavigateToDetailsHomeworkFromKanban(
                                    rowIndex = rowIndex,
                                    columnIndex = columnIndex
                                )
                            )
                        },
                        onStartDragAndDrop = { oldRowId, oldColumnId ->

                        },
                        onEndDragAndDrop = { oldRowId, oldColumnId, newRowId ->
                            handleIntent(
                                HomeworkListIntent.OnItemMoved(
                                    oldRowId = oldRowId,
                                    oldColumnId = oldColumnId,
                                    newRowId = newRowId
                                )
                            )
                        },
                        itemInDeleteBtn = { oldRowId, oldColumnId ->
                            handleIntent(
                                HomeworkListIntent.DeleteItemFromKanban(
                                    oldRowId = oldRowId,
                                    oldColumnId = oldColumnId
                                )
                            )
                        },
                        isItemDrag = { isDrag ->
                            if (isDrag) {
                                handleIntent(HomeworkListIntent.TurnFabInvisible)
                            } else {
                                handleIntent(HomeworkListIntent.TurnFabVisible)
                            }
                        }
                    )

                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        itemsIndexed(
                            uiState.homeworkList,
                            key = { _, model ->
                                model.id
                            }
                        ) { index, model ->
                            HomeworkCard(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .padding(vertical = 2.dp),
                                homeworkUiModel = model,
                                goToDetails = {
                                    handleIntent(
                                        HomeworkListIntent.NavigateToDetailsHomework(
                                            homeworkId = model.id,
                                            subjectId = model.subjectId
                                        )
                                    )
                                },
                                turnEditModeOn = {
                                    handleIntent(
                                        HomeworkListIntent.TurnEditMode(
                                            index = index
                                        )
                                    )
                                },
                                onCheckCardClicked = {
                                    handleIntent(
                                        HomeworkListIntent.CheckCard(
                                            index = index,
                                            isChecked = it
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    LocalHomeworkAndTaskManagerTheme {
        HomeworkListScreen(
            uiState = HomeworkListState(),
            handleIntent = {}
        )
    }
}