package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import ru.pkstudio.localhomeworkandtaskmanager.R
import ru.pkstudio.localhomeworkandtaskmanager.core.components.DefaultFloatingActionButton
import ru.pkstudio.localhomeworkandtaskmanager.core.components.DefaultTopAppBar
import ru.pkstudio.localhomeworkandtaskmanager.core.components.DeleteDialog
import ru.pkstudio.localhomeworkandtaskmanager.core.components.EmptyScreen
import ru.pkstudio.localhomeworkandtaskmanager.core.components.GradientFloatingActionButton
import ru.pkstudio.localhomeworkandtaskmanager.core.components.ImportanceColorPaletteDialog
import ru.pkstudio.localhomeworkandtaskmanager.core.components.Loading
import ru.pkstudio.localhomeworkandtaskmanager.core.components.TopAppBarAction
import ru.pkstudio.localhomeworkandtaskmanager.core.components.kanban.KanbanBoard
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList.components.HomeworkCard
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList.components.KanbanColumnFiller
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList.components.KanbanHeader
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.LocalHomeworkAndTaskManagerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeworkListScreen(
    uiState: HomeworkListState,
    handleIntent: (HomeworkListIntent) -> Unit,
) {
    var screenSize by rememberSaveable {
        mutableIntStateOf(0)
    }
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val lazyListState = rememberLazyListState()
    LaunchedEffect(key1 = lazyListState) {
        var isBtnVisible = uiState.isFABVisible
        snapshotFlow { lazyListState.firstVisibleItemScrollOffset }
            .collectLatest {
                if (!uiState.isEditModeEnabled) {
                    if (lazyListState.lastScrolledForward) {
                        if (isBtnVisible) {
                            isBtnVisible = false
                            handleIntent(HomeworkListIntent.TurnFabInvisible)
                        }
                    }
                    if (lazyListState.lastScrolledBackward) {
                        if (!isBtnVisible) {
                            isBtnVisible = true
                            handleIntent(HomeworkListIntent.TurnFabVisible)
                        }
                    }
                }
            }
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned {
                screenSize = it.size.width
            },
        topBar = {
            if (uiState.isEditModeEnabled) {
                DefaultTopAppBar(
                    modifier = Modifier.windowInsetsPadding(
                        WindowInsets.safeDrawing.only(
                            WindowInsetsSides.Horizontal
                        )
                    ),
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
                    modifier = Modifier.windowInsetsPadding(
                        WindowInsets.safeDrawing.only(
                            WindowInsetsSides.Horizontal
                        )
                    ),
                    title = uiState.subjectName,
                    navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                    navigationAction = {
                        handleIntent.invoke(HomeworkListIntent.NavigateUp)
                    },
                    actions = if (uiState.isKanbanScreenVisible) {
                        listOf(
                            TopAppBarAction(
                                image = Icons.Default.FilterList,
                                contentDescription = "",
                                action = {
                                    handleIntent(HomeworkListIntent.OnOpenBottomSheetClick)
                                },
                                tint = MaterialTheme.colorScheme.onSurface
                            ),
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
                        listOf(
                            TopAppBarAction(
                                image = Icons.Default.FilterList,
                                contentDescription = "",
                                action = {
                                    handleIntent(HomeworkListIntent.OnOpenBottomSheetClick)
                                },
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                )
            }
        },
        floatingActionButton = {
            if (uiState.isScreenEmpty) {
                GradientFloatingActionButton(
                    modifier = Modifier.windowInsetsPadding(
                        WindowInsets.safeDrawing.only(
                            WindowInsetsSides.End
                        )
                    ),
                    onClick = {
                        handleIntent.invoke(HomeworkListIntent.NavigateToAddHomework)
                    },
                    imageVector = Icons.Default.Add,
                )
            } else {
                AnimatedVisibility(
                    visible = uiState.isFABVisible,
                    enter = if (uiState.isKanbanScreenVisible) {
                        slideInHorizontally() + fadeIn()
                    } else {
                        slideInVertically(
                            initialOffsetY = {
                                it
                            }
                        ) + fadeIn()
                    },
                    exit = if (uiState.isKanbanScreenVisible) {
                        slideOutHorizontally() + fadeOut()
                    } else {
                        slideOutVertically(
                            targetOffsetY = {
                                it
                            }
                        ) + fadeOut()
                    },

                ) {
                    DefaultFloatingActionButton(
                        modifier = Modifier.windowInsetsPadding(
                            WindowInsets.safeDrawing.only(
                                WindowInsetsSides.End
                            )
                        ),
                        onClick = {
                            handleIntent.invoke(HomeworkListIntent.NavigateToAddHomework)
                        },
                        imageVector = Icons.Default.Add
                    )
                }
            }
        }
    ) { paddingValues ->
        val sheetState = rememberModalBottomSheetState()

        if (uiState.isSortBottomSheetOpen) {
            SortModalBottomSheet(
                sheetState = sheetState,
                isSortImportance = uiState.isSortImportance,
                onSortImportanceClick = {
                    handleIntent(HomeworkListIntent.OnSortClick(it))
                },
                onDismissRequest = {
                    handleIntent(HomeworkListIntent.CloseBottomSheet)
                }
            )
        }
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
            if (uiState.isColorPaletteDialogOpen) {
                ImportanceColorPaletteDialog(
                    colorList = uiState.colorList,
                    onSelectClick = {
                        handleIntent(HomeworkListIntent.SelectColor(it))
                    },
                    onDismiss = {
                        handleIntent(HomeworkListIntent.CloseCardColorPaletteDialog)
                    },
                    onBtnDismissClick = {
                        handleIntent(HomeworkListIntent.CloseCardColorPaletteDialog)
                    }
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
            ) {
               if (isPortrait) {
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
                   }
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
                        borderColorFromItem = { model ->
                            Color(model.color)
                        },
                        footer = {},
                        columnFiller = {
                            KanbanColumnFiller(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                model = it,
                                onColorPaletteClicked = {
                                    handleIntent(HomeworkListIntent.OnKanbanCardColorPaletteClicked(it.id))
                                }
                            )
                        },
                        columnWidth = if (isPortrait) {
                            with (density) {screenSize.toDp() - (screenSize / 7).toDp()}
                        } else {
                            uiState.kanbanItemsList.size
                            270.dp
                        },
                        columnBackgroundColor = Color.Transparent,
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
                        state = lazyListState,
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
                                            subjectId = uiState.subjectId
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
                                },
                                onColorPaletteClicked = {
                                    handleIntent(HomeworkListIntent.OnCardColorPaletteClicked(index))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortModalBottomSheet(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    isSortImportance: Boolean,
    onSortImportanceClick: (Boolean) -> Unit,
    onDismissRequest: () -> Unit,
) {
    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        onDismissRequest = {
            onDismissRequest()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                style = MaterialTheme.typography.headlineSmall,
                text = stringResource(R.string.sort)
            )

            Text(
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
                style = MaterialTheme.typography.titleMedium,
                text = stringResource(R.string.sort_by_importance),
                fontWeight = FontWeight.W700
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = stringResource(R.string.sort_by_descending_importance))
                RadioButton(
                    selected = isSortImportance,
                    onClick = {
                        onSortImportanceClick(true)
                    },
                )
            }

            Text(
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                style = MaterialTheme.typography.titleMedium,
                text = stringResource(R.string.sort_by_add_date),
                fontWeight = FontWeight.W700
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = stringResource(R.string.sort_by_descending_add_date))
                RadioButton(
                    selected = !isSortImportance,
                    onClick = {
                        onSortImportanceClick(false)
                    },
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    LocalHomeworkAndTaskManagerTheme {
        HomeworkListScreen(
            uiState = HomeworkListState(
                isLoading = false,
                isSortBottomSheetOpen = true
            ),
            handleIntent = {}
        )
    }
}