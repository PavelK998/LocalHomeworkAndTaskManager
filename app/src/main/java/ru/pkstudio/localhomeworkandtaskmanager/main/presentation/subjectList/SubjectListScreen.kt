package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.subjectList

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import ru.pkstudio.localhomeworkandtaskmanager.R
import ru.pkstudio.localhomeworkandtaskmanager.core.components.DefaultButton
import ru.pkstudio.localhomeworkandtaskmanager.core.components.DefaultFloatingActionButton
import ru.pkstudio.localhomeworkandtaskmanager.core.components.DeleteDialog
import ru.pkstudio.localhomeworkandtaskmanager.core.components.EmptyScreen
import ru.pkstudio.localhomeworkandtaskmanager.core.components.GradientFloatingActionButton
import ru.pkstudio.localhomeworkandtaskmanager.core.components.Loading
import ru.pkstudio.localhomeworkandtaskmanager.core.components.SwipeableCard
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.subjectList.components.SubjectCard
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.LocalHomeworkAndTaskManagerTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectListScreen(
    uiState: SubjectListState,
    handleIntent: (SubjectListIntent) -> Unit,
) {
    val lazyListState = rememberLazyListState()
    LaunchedEffect(key1 = lazyListState) {
        var isBtnVisible = uiState.isFABVisible
        snapshotFlow { lazyListState.firstVisibleItemScrollOffset }
            .collectLatest {
                if (lazyListState.lastScrolledForward) {
                    if (isBtnVisible) {
                        isBtnVisible = false
                        handleIntent(SubjectListIntent.TurnFabInvisible)
                    }
                }
                if (lazyListState.lastScrolledBackward) {
                    if (!isBtnVisible) {
                        isBtnVisible = true
                        handleIntent(SubjectListIntent.TurnFabVisible)
                    }
                }

            }
    }
    Scaffold(
        contentWindowInsets = WindowInsets.safeContent,
        topBar = {
            TopAppBar(
                modifier = Modifier.windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Start
                    )
                ),
                title = {
                    Text(
                        modifier = Modifier
                            .padding(start = 12.dp),
                        text = uiState.toolbarTitle
                    )
                },
                navigationIcon = {
                    IconButton(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            ),
                        onClick = {
                            handleIntent(SubjectListIntent.OnSettingClicked)
                        }
                    ) {
                        Image(
                            painter = painterResource(R.drawable.logo4),
                            contentDescription = ""
                        )
                    }
                },
            )
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
                        handleIntent(SubjectListIntent.OpenAddSubject)
                    },
                    imageVector = Icons.Default.Add,
                )
            } else {
                AnimatedVisibility(
                    visible = uiState.isFABVisible,
                    enter =
                        slideInVertically(
                            initialOffsetY = {
                                it
                            }
                        ) + fadeIn(),
                    exit =
                        slideOutVertically(
                            targetOffsetY = {
                                it
                            }
                        ) + fadeOut()
                ) {
                    DefaultFloatingActionButton(
                        modifier = Modifier.windowInsetsPadding(
                            WindowInsets.safeDrawing.only(
                                WindowInsetsSides.End
                            )
                        ),
                        onClick = {
                            handleIntent(SubjectListIntent.OpenAddSubject)
                        },
                        imageVector = Icons.Default.Add,
                    )
                }

            }


        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Loading()
        } else if (uiState.isScreenEmpty) {
            EmptyScreen(modifier = Modifier.padding(paddingValues))
        } else {
            LazyColumn(
                state = lazyListState,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(
                        top = 24.dp,
                        bottom = 20.dp
                    )
                    .padding(horizontal = 12.dp),
            ) {
                itemsIndexed(
                    uiState.subjectsList,
                    key = { _, model ->
                        model.id
                    }
                ) { index, model ->
                    SwipeableCard(
                        modifier = Modifier
                            .padding(vertical = 2.dp)
                            .padding(horizontal = 8.dp),
                        isRevealed = model.isRevealed,
                        actions = {
                            IconButton(
                                onClick = {
                                    handleIntent(SubjectListIntent.DeleteSubject(index))
                                }
                            ) {
                                Icon(
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null
                                )
                            }
                            IconButton(
                                onClick = {
                                    handleIntent(SubjectListIntent.TurnEditModeOn(index))
                                }
                            ) {
                                Icon(
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null
                                )
                            }
                        },
                        onCollapsed = {
                            handleIntent(
                                SubjectListIntent.OnRevealCardOptionsMenuClicked(
                                    index = index,
                                    isRevealed = false
                                )
                            )
                        },
                        onExpanded = {
                            handleIntent(
                                SubjectListIntent.OnRevealCardOptionsMenuClicked(
                                    index = index,
                                    isRevealed = true
                                )
                            )
                        }
                    ) {
                        SubjectCard(
                            isEditModeEnabled = model.isEditModeEnabled,
                            navigateToHomeworkScreen = {
                                handleIntent(
                                    SubjectListIntent.NavigateToHomeworkScreen(
                                        subjectNane = model.subjectName,
                                        subjectId = model.id
                                    )
                                )
                            },
                            onTitleChanged = {
                                handleIntent(SubjectListIntent.OnEditTitleChanged(it))
                            },
                            onCommentChanged = {
                                handleIntent(SubjectListIntent.OnEditCommentChanged(it))
                            },
                            onConfirmChangesBtnCLicked = {
                                handleIntent(SubjectListIntent.EditSubject(index))
                            },
                            onDiscardChangesBtnCLicked = {
                                handleIntent(SubjectListIntent.TurnEditModeOff(index))
                            },
                            title = model.subjectName,
                            comment = model.comment,
                            editModeTitle = uiState.subjectNameForEdit,
                            editModeComment = uiState.subjectCommentForEdit,
                        )
                    }
                }
            }
        }
    }

    if (uiState.isDeleteAlertDialogOpened) {
        DeleteDialog(
            title = uiState.titleDeleteAlertDialog,
            comment = uiState.commentDeleteAlertDialog,
            onConfirm = {
                handleIntent(SubjectListIntent.ConfirmDeleteSubject)
            },
            onDismissRequest = {
                handleIntent(SubjectListIntent.CloseDeleteDialog)
            },
            onDismiss = {
                handleIntent(SubjectListIntent.CloseDeleteDialog)
            }
        )
    }

    if (uiState.isAddSubjectAlertDialogOpened) {
        AlertDialog(
            title = {
                Text(
                    style = MaterialTheme.typography.headlineSmall,
                    text = uiState.titleAddDialog
                )
            },
            onDismissRequest = {
                handleIntent(SubjectListIntent.CloseAddSubject)
            },
            confirmButton = {
                DefaultButton(
                    onClick = {
                        handleIntent(SubjectListIntent.AddSubject)
                    },
                    text = stringResource(id = R.string.confirm_button),
                    textStyle = MaterialTheme.typography.bodyMedium,
                )

            },
            dismissButton = {
                DefaultButton(
                    modifier = Modifier.padding(end = 8.dp),
                    onClick = {
                        handleIntent(SubjectListIntent.CloseAddSubject)
                    },
                    text = stringResource(id = R.string.dismiss_button),
                    textStyle = MaterialTheme.typography.bodyMedium

                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    TextField(
                        value = uiState.newSubjectName,
                        colors = TextFieldDefaults.colors().copy(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        ),
                        label = {
                            Text(text = stringResource(id = R.string.name_subject))
                        },
                        onValueChange = {
                            handleIntent(SubjectListIntent.ChangeNameSubject(it))
                        }
                    )

                    TextField(
                        modifier = Modifier.padding(top = 12.dp),
                        value = uiState.newSubjectComment,
                        colors = TextFieldDefaults.colors().copy(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        ),
                        label = {
                            Text(text = stringResource(id = R.string.comment))
                        },
                        onValueChange = {
                            handleIntent(SubjectListIntent.ChangeCommentSubject(it))
                        }
                    )
                }
            }
        )
    }
}


@Preview(showSystemUi = true)
@Composable
private fun Preview() {
    LocalHomeworkAndTaskManagerTheme {
        SubjectListScreen(
            uiState = SubjectListState(
                isLoading = false,
                isScreenEmpty = false
            ),
            handleIntent = {},
        )
    }
}