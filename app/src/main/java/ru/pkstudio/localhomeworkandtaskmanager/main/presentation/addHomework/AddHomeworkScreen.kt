package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.addHomework

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults
import kotlinx.coroutines.delay
import ru.pkstudio.localhomeworkandtaskmanager.R
import ru.pkstudio.localhomeworkandtaskmanager.core.components.DefaultDatePicker
import ru.pkstudio.localhomeworkandtaskmanager.core.components.DefaultTimePicker
import ru.pkstudio.localhomeworkandtaskmanager.core.components.DefaultTopAppBar
import ru.pkstudio.localhomeworkandtaskmanager.core.components.DeleteDialog
import ru.pkstudio.localhomeworkandtaskmanager.core.components.EditTextPanel
import ru.pkstudio.localhomeworkandtaskmanager.core.components.ImportanceAndStageSelector
import ru.pkstudio.localhomeworkandtaskmanager.core.components.ImportanceColorPaletteDialog
import ru.pkstudio.localhomeworkandtaskmanager.core.components.StagePickerDialog
import ru.pkstudio.localhomeworkandtaskmanager.core.components.TopAppBarAction
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.LocalHomeworkAndTaskManagerTheme
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.loadingBackground
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.stageVariant8
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHomeworkScreen(
    uiState: AddHomeworkState,
    handleIntent: (AddHomeworkIntent) -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(
        uiState.isNameCardVisible
    ) {
        if (uiState.isNameCardVisible) {
            delay(100)
            focusRequester.requestFocus()
        }
    }

    LaunchedEffect(
        uiState.isDescriptionCardVisible
    ) {
        if (uiState.isDescriptionCardVisible) {
            delay(100)
            focusRequester.requestFocus()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.surface,
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
                DefaultTopAppBar(
                    modifier = Modifier.windowInsetsPadding(
                        WindowInsets.safeDrawing.only(
                            WindowInsetsSides.Horizontal
                        )
                    ),
                    title = "",
                    navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                    navigationAction = {
                        handleIntent(AddHomeworkIntent.NavigateUp)
                    },
                    actions = listOf(
                        TopAppBarAction(
                            imageRes = R.drawable.icon_attach,
                            contentDescription = "Select media",
                            action = {
                                handleIntent(AddHomeworkIntent.OnSelectMediaClick)
                            },
                            tint = MaterialTheme.colorScheme.onSurface
                        ),
                        TopAppBarAction(
                            image = Icons.Filled.Done,
                            contentDescription = "Done",
                            action = {
                                handleIntent.invoke(AddHomeworkIntent.Save)
                            },
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    )
                )
            }
        ) { paddingValues ->
            if (uiState.isSelectFilePathDialogOpened) {
                DeleteDialog(
                    title = stringResource(R.string.select_folder_dialog_title),
                    comment = stringResource(R.string.select_folder_dialog_description),
                    onConfirm = {
                        handleIntent(AddHomeworkIntent.ConfirmPathSelect)
                    },
                    onDismissRequest = {
                        handleIntent(AddHomeworkIntent.DismissPathSelectDialog)
                    },
                    onDismiss = {
                        handleIntent(AddHomeworkIntent.DismissPathSelectDialog)
                    }
                )
            }

            if (uiState.isColorPickerVisible) {
                ImportanceColorPaletteDialog(
                    colorList = uiState.colorList,
                    onSelectClick = {
                        handleIntent(AddHomeworkIntent.SelectImportanceColor(it))
                    },
                    onBtnDismissClick = {
                        handleIntent(AddHomeworkIntent.CloseImportanceColorDialog)
                    },
                    onDismiss = {
                        handleIntent(AddHomeworkIntent.CloseImportanceColorDialog)
                    }
                )
            }
            if (uiState.isStagePickerVisible) {
                StagePickerDialog(
                    stageList = uiState.stageList,
                    onSelectStageClick = {
                        handleIntent(AddHomeworkIntent.SelectStage(it))
                    },
                    onDismiss = {
                        handleIntent(AddHomeworkIntent.CloseStagePickerDialog)
                    },
                    onCreateStageBtnClick = {
                        handleIntent(AddHomeworkIntent.NavigateToEditStages)
                    }
                )
            }
            if (uiState.isDatePickerVisible) {
                DefaultDatePicker(
                    onDismiss = {
                        handleIntent(AddHomeworkIntent.CloseDatePickerDialog)
                    },
                    onConfirm = {
                        handleIntent(AddHomeworkIntent.DatePicked(it ?: 0L))
                    }
                )
            }
            if (uiState.isTimePickerVisible) {
                DefaultTimePicker(
                    onConfirm = { timePickerState ->
                        handleIntent(
                            AddHomeworkIntent.TimePicked(
                                LocalTime.of(timePickerState.hour, timePickerState.minute)
                                    .toString()
                            )
                        )
                    },
                    onDismiss = {
                        handleIntent(AddHomeworkIntent.CloseTimePickerDialog)
                    }
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .weight(7f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Top
                ) {
                    ImportanceAndStageSelector(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(top = 4.dp, bottom = 8.dp),
                        currentStageName = uiState.currentSelectedStage?.stageName ?: "",
                        currentColor = uiState.currentColor,
                        ifStageIsFinishStage = uiState.currentSelectedStage?.isFinishStage ?: false,
                        onColorSelectClick = {
                            handleIntent(AddHomeworkIntent.OpenImportanceColorDialog)
                        },
                        onStageSelectClick = {
                            handleIntent(AddHomeworkIntent.OpenStagePickerDialog)
                        },
                        currentStageColor = uiState.currentSelectedStage?.color
                            ?: stageVariant8.toArgb()
                    )
                    if (uiState.currentSelectedStage?.isFinishStage == false) {
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.finish_before)
                            )
                            TextButton(
                                onClick = {
                                    handleIntent(AddHomeworkIntent.SelectDateTime)
                                }
                            ) {
                                Text(
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    text = if (uiState.selectedFinishDate.isBlank() || uiState.selectedFinishTime.isBlank()) {
                                        stringResource(R.string.select_date)
                                    } else {
                                        "${uiState.selectedFinishDate} ${uiState.selectedFinishTime}"
                                    }

                                )
                            }

                        }
                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        thickness = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .heightIn(
                                min = 70.dp
                            )
                            .padding(horizontal = 16.dp)

                            .clickable {
                                handleIntent(AddHomeworkIntent.OnNameChangeClick)
                            }
                    ) {
                        if (uiState.nameRichTextState.annotatedString.text.isNotBlank()) {
                            RichText(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .padding(top = 4.dp)
                                    .clickable {
                                        handleIntent(AddHomeworkIntent.OnNameChangeClick)
                                    },
                                state = uiState.nameRichTextState,
                                style = TextStyle(fontSize = uiState.nameRichTextState.currentSpanStyle.fontSize)
                            )
                        } else {
                            Text(
                                modifier = Modifier
                                    .padding(start = 4.dp, top = 8.dp),
                                style = MaterialTheme.typography.titleLarge,
                                text = stringResource(R.string.add_homework_title_label),
                                color = Color.Gray
                            )
                        }

                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 200.dp)
                            .padding(horizontal = 16.dp)
                            .clickable {
                                handleIntent(AddHomeworkIntent.OnDescriptionChangeClick)
                            }
                    ) {
                        if (uiState.descriptionRichTextState.annotatedString.text.isNotBlank()) {
                            RichText(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .padding(top = 4.dp)
                                    .clickable {
                                        handleIntent(AddHomeworkIntent.OnDescriptionChangeClick)
                                    },
                                state = uiState.descriptionRichTextState,
                                style = TextStyle(fontSize = uiState.nameRichTextState.currentSpanStyle.fontSize)
                            )
                        } else {
                            Text(
                                modifier = Modifier
                                    .padding(start = 4.dp, top = 8.dp),
                                style = MaterialTheme.typography.titleMedium,
                                text = stringResource(R.string.add_homework_description_label),
                                color = Color.Gray
                            )
                        }
                    }

                }
                AnimatedVisibility(uiState.imagesUriList.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier
                            .padding(4.dp)
                            .weight(1f),
                    ) {
                        itemsIndexed(
                            items = uiState.imagesUriList,
                            key = { _, item ->
                                item
                            }
                        ) { index, image ->
                            ImageCard(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .size(90.dp),
                                uri = image,
                                onDeleteClick = {
                                    handleIntent(AddHomeworkIntent.OnDeleteImage(index))
                                }
                            )
                        }
                    }
                }
            }


        }
        AnimatedVisibility(
            visible = uiState.isNameCardVisible,
            enter = slideInVertically(initialOffsetY = { it / 2 }),
            exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut()
        ) {
            SetName(
                modifier = Modifier.fillMaxSize(),
                nameRichTextState = uiState.nameRichTextState,
                handleIntent = handleIntent,
                focusRequester = focusRequester,
                isExtraOptionsVisible = uiState.isNameExtraOptionsVisible
            )
        }

        AnimatedVisibility(
            visible = uiState.isDescriptionCardVisible,
            enter = slideInVertically(initialOffsetY = { it / 2 }),
            exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut()
        ) {

            SetDescription(
                modifier = Modifier.fillMaxSize(),
                descriptionRichTextState = uiState.descriptionRichTextState,
                handleIntent = handleIntent,
                focusRequester = focusRequester,
                isExtraOptionsVisible = uiState.isDescriptionExtraOptionsVisible
            )
        }

        AnimatedVisibility(
            visible = uiState.isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            AddScreenLoading()
        }

    }

}

@Composable
fun AddScreenLoading(
    color: Color = MaterialTheme.colorScheme.secondary,
    trackColor: Color = MaterialTheme.colorScheme.onSecondary,
    strokeWidth: Dp = 5.dp,
    strokeCap: StrokeCap = StrokeCap.Round
) {
    val density = LocalDensity.current
    var isFirstTextVisible by rememberSaveable {
        mutableStateOf(true)
    }
    var isSecondTextVisible by rememberSaveable {
        mutableStateOf(false)
    }
    var spacerHeight by rememberSaveable {
        mutableIntStateOf(0)
    }
    LaunchedEffect(true) {
        delay(2500)
        isFirstTextVisible = false
        delay(500)
        isSecondTextVisible = true
    }

    Column(
        modifier = Modifier.fillMaxSize().background(loadingBackground),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = color,
            strokeWidth = strokeWidth,
            trackColor = trackColor,
            strokeCap = strokeCap
        )

        Box(
            contentAlignment = Alignment.Center
        ) {
            if (!isFirstTextVisible && !isSecondTextVisible) {
                Spacer(
                    modifier = Modifier.height(
                        with(density) { spacerHeight.toDp() }
                    )
                )
            }
            androidx.compose.animation.AnimatedVisibility(
                modifier = Modifier.onGloballyPositioned {
                    if (spacerHeight != it.size.height) {
                        spacerHeight = it.size.height
                    }
                },
                visible = isFirstTextVisible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    style = MaterialTheme.typography.titleLarge,
                    text = stringResource(R.string.loading),
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            androidx.compose.animation.AnimatedVisibility(
                isSecondTextVisible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    style = MaterialTheme.typography.titleLarge,
                    text = stringResource(R.string.loading_more_time),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SetName(
    modifier: Modifier = Modifier,
    nameRichTextState: RichTextState,
    isExtraOptionsVisible: Boolean,
    handleIntent: (AddHomeworkIntent) -> Unit,
    focusRequester: FocusRequester
) {

    BackHandler {
        handleIntent(AddHomeworkIntent.CloseNameChangeCard)
    }
    Scaffold { paddingValues ->
        val density = LocalDensity.current
        val imeInsets = WindowInsets.ime.getBottom(density)
        val defaultBottomPadding = paddingValues.calculateBottomPadding()
        var bottomPadding by remember {
            mutableStateOf(defaultBottomPadding)
        }

        LaunchedEffect(imeInsets) {
            val imeInsetsToDp = with(density) { imeInsets.toDp() }
            bottomPadding = if (imeInsetsToDp <= defaultBottomPadding) {
                defaultBottomPadding
            } else {
                with(density) { imeInsets.toDp() }
            }
        }
        Column(
            modifier = modifier
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = bottomPadding
                )
        ) {
            Card(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 24.dp),
                colors = CardDefaults.cardColors().copy(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        style = MaterialTheme.typography.titleLarge,
                        text = stringResource(R.string.name)
                    )
                    IconButton(
                        onClick = {
                            handleIntent(AddHomeworkIntent.CloseNameChangeCard)
                        }
                    ) {
                        Icon(imageVector = Icons.Filled.Check, contentDescription = "")
                    }
                }
                RichTextEditor(
                    textStyle = TextStyle(fontSize = nameRichTextState.currentSpanStyle.fontSize),
                    modifier = Modifier.focusRequester(focusRequester),
                    state = nameRichTextState,
                    colors = RichTextEditorDefaults.richTextEditorColors(
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        containerColor = Color.Transparent
                    ),
                    placeholder = {
                        Text(
                            fontSize = 20.sp,
                            text = stringResource(R.string.add_homework_title_label)
                        )
                    },
                )
            }

            EditTextPanel(
                textState = nameRichTextState,
                isExtraOptionsVisible = isExtraOptionsVisible,
                onBoldBtnClick = {
                    handleIntent(AddHomeworkIntent.ToggleNameBold)
                },
                onItalicBtnClick = {
                    handleIntent(AddHomeworkIntent.ToggleNameItalic)
                },
                onLineThroughBtnClick = {
                    handleIntent(AddHomeworkIntent.ToggleNameLineThrough)
                },
                onUnderlinedBtnClick = {
                    handleIntent(AddHomeworkIntent.ToggleNameUnderline)
                },
                onFontSizeChange = {
                    handleIntent(AddHomeworkIntent.NameFontSizeChange(it))
                },
                onExtraOptionsBtnClick = {
                    handleIntent(AddHomeworkIntent.ToggleNameExtraOptions)
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SetDescription(
    modifier: Modifier = Modifier,
    descriptionRichTextState: RichTextState,
    isExtraOptionsVisible: Boolean,
    handleIntent: (AddHomeworkIntent) -> Unit,
    focusRequester: FocusRequester
) {
    BackHandler {
        handleIntent(AddHomeworkIntent.CloseDescriptionChangeCard)
    }
    Scaffold { paddingValues ->
        val density = LocalDensity.current
        val imeInsets = WindowInsets.ime.getBottom(density)
        val defaultBottomPadding = paddingValues.calculateBottomPadding()
        var bottomPadding by remember {
            mutableStateOf(defaultBottomPadding)
        }

        LaunchedEffect(imeInsets) {
            val imeInsetsToDp = with(density) { imeInsets.toDp() }
            bottomPadding = if (imeInsetsToDp <= defaultBottomPadding) {
                defaultBottomPadding
            } else {
                with(density) { imeInsets.toDp() }
            }
        }
        Column(
            modifier = modifier
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = bottomPadding
                ),
        ) {
            Card(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 24.dp),
                colors = CardDefaults.cardColors().copy(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        style = MaterialTheme.typography.titleLarge,
                        text = stringResource(R.string.description)
                    )
                    IconButton(
                        onClick = {
                            handleIntent(AddHomeworkIntent.CloseDescriptionChangeCard)
                        }
                    ) {
                        Icon(imageVector = Icons.Filled.Check, contentDescription = "")
                    }
                }
                RichTextEditor(
                    modifier = Modifier.focusRequester(focusRequester),
                    state = descriptionRichTextState,
                    colors = RichTextEditorDefaults.richTextEditorColors(
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        containerColor = Color.Transparent
                    ),
                    placeholder = {
                        Text(text = stringResource(id = R.string.add_homework_description_label))
                    }
                )
            }
            EditTextPanel(
                textState = descriptionRichTextState,
                isExtraOptionsVisible = isExtraOptionsVisible,
                onBoldBtnClick = {
                    handleIntent(AddHomeworkIntent.ToggleDescriptionBold)
                },
                onItalicBtnClick = {
                    handleIntent(AddHomeworkIntent.ToggleDescriptionItalic)
                },
                onUnderlinedBtnClick = {
                    handleIntent(AddHomeworkIntent.ToggleDescriptionUnderline)
                },
                onLineThroughBtnClick = {
                    handleIntent(AddHomeworkIntent.ToggleDescriptionLineThrough)
                },
                onFontSizeChange = {
                    handleIntent(AddHomeworkIntent.DescriptionFontSizeChange(it))
                },
                onExtraOptionsBtnClick = {
                    handleIntent(AddHomeworkIntent.ToggleDescriptionExtraOptions)
                }
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    LocalHomeworkAndTaskManagerTheme {
        AddHomeworkScreen(
            uiState = AddHomeworkState(),
            handleIntent = {}
        )
    }
}