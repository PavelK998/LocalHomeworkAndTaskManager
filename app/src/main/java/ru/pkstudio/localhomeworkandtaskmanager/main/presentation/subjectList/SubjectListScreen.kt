package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.subjectList

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.pakarpichev.homeworktool.core.presentation.components.DefaultTopAppBar
import ru.pkstudio.localhomeworkandtaskmanager.R
import ru.pkstudio.localhomeworkandtaskmanager.core.components.DefaultButton
import ru.pkstudio.localhomeworkandtaskmanager.core.components.DefaultFloatingActionButton
import ru.pkstudio.localhomeworkandtaskmanager.core.components.EmptyScreen
import ru.pkstudio.localhomeworkandtaskmanager.core.components.Loading
import ru.pkstudio.localhomeworkandtaskmanager.core.components.SwipeableCard
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.subjectList.components.SubjectCard
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.LocalHomeworkAndTaskManagerTheme


@Composable
fun SubjectListScreen(
    uiState: SubjectListState,
    handleIntent: (SubjectListIntent) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(100.dp)

                    ) {
                        Image(
                            modifier = Modifier.size(100.dp),
                            painter = painterResource(id = R.drawable.logo4),
                            contentDescription = "logo"
                        )
                    }
                }
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                DefaultTopAppBar(
                    title = stringResource(id = R.string.subjects),
                    navigationIcon = Icons.Default.Menu,
                    navigationAction = {
                        scope.launch {
                            drawerState.open()
                        }
                    },
                    actions = emptyList()
                )
            },
            floatingActionButton = {
                DefaultFloatingActionButton(
                    onClick = {
                        handleIntent(SubjectListIntent.OpenAddSubject)
                    },
                    imageVector = Icons.Default.Add
                )
            }
        ) { paddingValues ->
            if (uiState.isLoading) {
                Loading()
            } else if (uiState.isScreenEmpty) {
                EmptyScreen(modifier = Modifier.padding(paddingValues))
            } else {
                LazyColumn(
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
    }

    if (uiState.isAddSubjectAlertDialogOpened) {
        AlertDialog(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            title = {
                Text(
                    style = MaterialTheme.typography.headlineSmall,
                    text = stringResource(id = R.string.add_new_subject)
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
                TextField(
                    value = uiState.newSubjectName,
                    colors = TextFieldDefaults.colors().copy(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        focusedIndicatorColor = MaterialTheme.colorScheme.onTertiary,
                    ),
                    onValueChange = {
                        handleIntent(SubjectListIntent.ChangeNameSubject(it))
                    }
                )
            }
        )
    }
}

@Preview
@Composable
private fun Preview() {
    LocalHomeworkAndTaskManagerTheme {
        SubjectListScreen(
            uiState = SubjectListState(),
            handleIntent = {}
        )
    }
}