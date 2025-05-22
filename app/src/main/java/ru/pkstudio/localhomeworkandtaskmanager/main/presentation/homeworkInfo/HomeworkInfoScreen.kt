package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkInfo

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.pkstudio.localhomeworkandtaskmanager.R
import ru.pkstudio.localhomeworkandtaskmanager.core.components.DefaultDatePicker
import ru.pkstudio.localhomeworkandtaskmanager.core.components.DefaultTimePicker
import ru.pkstudio.localhomeworkandtaskmanager.core.components.DefaultTopAppBar
import ru.pkstudio.localhomeworkandtaskmanager.core.components.DeleteDialog
import ru.pkstudio.localhomeworkandtaskmanager.core.components.EditTextPanel
import ru.pkstudio.localhomeworkandtaskmanager.core.components.ImportanceAndStageSelector
import ru.pkstudio.localhomeworkandtaskmanager.core.components.ImportanceColorPaletteDialog
import ru.pkstudio.localhomeworkandtaskmanager.core.components.Loading
import ru.pkstudio.localhomeworkandtaskmanager.core.components.StagePickerDialog
import ru.pkstudio.localhomeworkandtaskmanager.core.components.TopAppBarAction
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.LocalHomeworkAndTaskManagerTheme
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.onDarkCardText
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.photoUiBackground
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.stageVariant8
import java.time.LocalTime


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeworkInfoScreen(
    uiState: HomeworkInfoState,
    handleIntent: (HomeworkInfoIntent) -> Unit,
) {
    BackHandler {
        handleIntent(HomeworkInfoIntent.NavigateUp)
    }
    val scrollState = rememberScrollState()
    val focusRequester = remember { FocusRequester() }
    val pagerState = rememberPagerState(
        initialPage = uiState.currentPagerPage.ordinal,
        pageCount = { HomeworkInfoPagerState.entries.size }
    )
    val scope = rememberCoroutineScope()
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
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            InfoTopBar(
                isMenuOpened = uiState.isSettingsMenuOpened,
                navigateUp = { handleIntent(HomeworkInfoIntent.NavigateUp) },
                onSettingsClicked = { handleIntent(HomeworkInfoIntent.OnSettingsClicked) },
                closeSettingsMenu = { handleIntent(HomeworkInfoIntent.CloseSettingsMenu) },
                onDeleteBtnClick = { handleIntent(HomeworkInfoIntent.OnDeleteBtnClick) }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Loading()
        } else {
            if (uiState.isColorPickerVisible) {
                ImportanceColorPaletteDialog(
                    colorList = uiState.colorList,
                    onSelectClick = {
                        handleIntent(HomeworkInfoIntent.SelectImportanceColor(it))
                    },
                    onBtnDismissClick = {
                        handleIntent(HomeworkInfoIntent.CloseImportanceColorDialog)
                    },
                    onDismiss = {
                        handleIntent(HomeworkInfoIntent.CloseImportanceColorDialog)
                    }
                )
            }
            if (uiState.isStagePickerVisible) {
                StagePickerDialog(
                    stageList = uiState.stageList,
                    onSelectStageClick = {
                        handleIntent(HomeworkInfoIntent.SelectStage(it))
                    },
                    onDismiss = {
                        handleIntent(HomeworkInfoIntent.CloseStagePickerDialog)
                    },
                    onCreateStageBtnClick = {
                        handleIntent(HomeworkInfoIntent.NavigateToEditStages)
                    }
                )
            }
            if (uiState.isDeleteDialogOpened) {
                DeleteDialog(
                    title = uiState.deleteDialogTitle,
                    comment = uiState.deleteDialogDescription,
                    onDismissRequest = {
                        handleIntent(HomeworkInfoIntent.CloseDeleteAlertDialog)
                    },
                    onConfirm = {
                        handleIntent(HomeworkInfoIntent.DeleteConfirm)
                        handleIntent(HomeworkInfoIntent.CloseDeleteAlertDialog)
                    },
                    onDismiss = {
                        handleIntent(HomeworkInfoIntent.CloseDeleteAlertDialog)
                    }
                )
            }
            if (uiState.isUpdateDialogOpened) {
                DeleteDialog(
                    title = uiState.updateDialogTitle,
                    comment = uiState.updateDialogDescription,
                    onDismissRequest = {
                        handleIntent(HomeworkInfoIntent.CloseUpdateAlertDialog)
                    },
                    onConfirm = {
                        handleIntent(HomeworkInfoIntent.UpdateConfirm)
                    },
                    onDismiss = {
                        handleIntent(HomeworkInfoIntent.UpdateDismiss)
                    }
                )
            }
            if (uiState.isDatePickerVisible) {
                DefaultDatePicker(
                    onDismiss = {
                        handleIntent(HomeworkInfoIntent.CloseDatePickerDialog)
                    },
                    onConfirm = {
                        Log.d("sdfsdfsdfsdf", "AddHomeworkScreen: $it")
                        handleIntent(HomeworkInfoIntent.DatePicked(it ?: 0L))
                    }
                )
            }
            if (uiState.isTimePickerVisible) {
                DefaultTimePicker(
                    onConfirm = { timePickerState ->
                        handleIntent(
                            HomeworkInfoIntent.TimePicked(
                                LocalTime.of(timePickerState.hour, timePickerState.minute)
                                    .toString()
                            )
                        )
                    },
                    onDismiss = {
                        handleIntent(HomeworkInfoIntent.CloseTimePickerDialog)
                    }
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
//                indicator = { tabPosition ->
//                    if(pagerState.currentPage < tabPosition.size) {
//                        TabRowDefaults
//                            .PrimaryIndicator(
//                                modifier = Modifier
//                                    .tabIndicatorOffset(
//                                        tabPosition[pagerState.currentPage]
//                                    ),
//                                height = 4.dp,
//                                width = 65.dp,
//                                color = Depth400,
//                                shape = RoundedCornerShape(
//                                    topStart = 10.dp,
//                                    topEnd = 10.dp
//                                )
//                            )
//                    }
//                }
                ) {
                    HomeworkInfoPagerState.entries.forEach { homeworkInfoPagerState ->
                        Tab(
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .padding(bottom = 5.dp),
                            selected = pagerState.currentPage == homeworkInfoPagerState.ordinal,
                            unselectedContentColor = Color.Black,
                            selectedContentColor = Color.Black,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(homeworkInfoPagerState.ordinal)
                                }
                            }
                        ) {
                            when (homeworkInfoPagerState) {
                                HomeworkInfoPagerState.MAIN -> {
                                    Icon(
                                        imageVector = Icons.Outlined.Description,
                                        contentDescription = "",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }

                                HomeworkInfoPagerState.MEDIA -> {
                                    Icon(
                                        imageVector = Icons.Outlined.PhotoLibrary,
                                        contentDescription = "",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
                HorizontalPager(
                    state = pagerState
                ) { index ->
                    when (HomeworkInfoPagerState.entries[index]) {
                        HomeworkInfoPagerState.MAIN -> {
                            MainScreen(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 8.dp)
                                    .verticalScroll(scrollState),
                                uiState = uiState,
                                handleIntent = handleIntent
                            )
                        }

                        HomeworkInfoPagerState.MEDIA -> {
                            MediaScreen(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 8.dp),
                                uiState = uiState,
                                onPhotoClicked = {
                                    handleIntent(HomeworkInfoIntent.OnPhotoClicked(it))
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    AnimatedVisibility(
        visible = uiState.isPhotoOpened,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        HandlePhotos(
            whichPhotoShouldBeOpenFirst = uiState.whichPhotoShouldBeOpenedFirst,
            listPhotos = uiState.photoList,
            isUiVisible = uiState.isPhotoUiVisible,
            onClick = {
                handleIntent(HomeworkInfoIntent.HandlePhotoUi(isVisible = it))
            },
            onBackCLick = {
                handleIntent(HomeworkInfoIntent.ClosePhotoMode)
            }
        )
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
}

@Composable
private fun MediaScreen(
    modifier: Modifier = Modifier,
    uiState: HomeworkInfoState,
    onPhotoClicked: (Int) -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        columns = GridCells.Adaptive(100.dp),
        content = {
            itemsIndexed(uiState.photoList) { index, photo ->
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clickable {
                            onPhotoClicked(index)
                        }
                ) {
                    Image(
                        contentScale = ContentScale.Crop,
                        bitmap = photo,
                        contentDescription = ""
                    )
                }
            }
        }
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun HandlePhotos(
    whichPhotoShouldBeOpenFirst: Int,
    isUiVisible: Boolean,
    listPhotos: List<ImageBitmap>,
    onClick: (Boolean) -> Unit,
    onBackCLick: () -> Unit
) {
    val context = LocalContext.current
    val activity by remember {
        mutableStateOf(context as ComponentActivity)
    }
    val windowInsetsController by remember {
        mutableStateOf(WindowCompat.getInsetsController(activity.window, activity.window.decorView))
    }
    var topPadding by remember {
        mutableStateOf(0.dp)
    }
    val isDarkTheme = isSystemInDarkTheme()
    BackHandler {
        onBackCLick()
        WindowCompat.getInsetsController(
            activity.window,
            activity.window.decorView
        ).isAppearanceLightStatusBars = !isDarkTheme
        windowInsetsController.show(androidx.core.view.WindowInsetsCompat.Type.statusBars())
    }
    LaunchedEffect(isUiVisible) {
        if (isUiVisible) {
            WindowCompat.getInsetsController(
                activity.window,
                activity.window.decorView
            ).isAppearanceLightStatusBars = false
            windowInsetsController.show(androidx.core.view.WindowInsetsCompat.Type.statusBars())
        } else {
            windowInsetsController.hide(androidx.core.view.WindowInsetsCompat.Type.statusBars())
        }
    }
//    var scale by remember { mutableFloatStateOf(1f) }
//    val animatedScale = animateFloatAsState(scale)
//    var x by remember { mutableFloatStateOf(0f) }
//    var y by remember { mutableFloatStateOf(0f) }
    val pagerState = rememberPagerState(
        initialPage = whichPhotoShouldBeOpenFirst,
        pageCount = {
            listPhotos.size
        }
    )
//    val maxOffsetX by remember(key1 = pagerState.currentPage, key2 = scale) {
//        mutableFloatStateOf((scale - 1) * listPhotos[pagerState.currentPage].width + 20f / 2)
//    }
//    val maxOffsetY by remember(key1 = pagerState.currentPage, key2 = scale) {
//        mutableFloatStateOf((scale - 1) * listPhotos[pagerState.currentPage].height + 20f / 2)
//    }
//    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
//        scale = (scale * zoomChange).coerceIn(1f, 5f)
//        x = (x + panChange.x).coerceIn(-maxOffsetX, maxOffsetX)
//        y = (y + panChange.y).coerceIn(-maxOffsetY, maxOffsetY)
//    }



    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        LaunchedEffect(true) {
            topPadding = paddingValues.calculateTopPadding()
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(isUiVisible) {
                    detectTapGestures(
                        onTap = {
                            onClick(isUiVisible)
                        },
                        onDoubleTap = {
//                            when (scale) {
//                                1f -> scale = 2f
//                                2f -> scale = 3f
//                                else -> {
//                                    x = 0f
//                                    y = 0f
//                                    scale = 1f
//                                }
//                            }
                        }
                    )
                },
        ) {
            HorizontalPager(
                state = pagerState
            ) { index ->
                Image(
                    modifier = Modifier
                        .fillMaxSize(),
//                        .transformable(state = transformableState)
//                        .graphicsLayer(
//                            scaleX = animatedScale.value,
//                            scaleY = animatedScale.value,
//                            translationX = x,
//                            translationY = y
//                        ),
                    bitmap = listPhotos[index],
                    contentDescription = ""
                )
            }
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                AnimatedVisibility(
                    visible = isUiVisible,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(53.dp + topPadding)
                            .background(photoUiBackground),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            modifier = Modifier.padding(top = 30.dp),
                            onClick = {
                                onBackCLick()
                                WindowCompat.getInsetsController(
                                    activity.window,
                                    activity.window.decorView
                                ).isAppearanceLightStatusBars = !isDarkTheme
                                windowInsetsController.show(androidx.core.view.WindowInsetsCompat.Type.statusBars())
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = "",
                                tint = onDarkCardText
                            )
                        }
//                        Text(
//                            modifier = Modifier.padding(start = 4.dp),
//                            style = MaterialTheme.typography.titleLarge,
//                            color = onDarkCardText,
//                            text = "${pagerState.currentPage + 1} ${stringResource(R.string.of)} ${listPhotos.size}"
//                        )
                    }
                }
                AnimatedVisibility(
                    visible = isUiVisible,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(15.dp + paddingValues.calculateBottomPadding())
                            .background(photoUiBackground)
                    ) {}
                }

            }
        }
    }
}

@Composable
private fun MainScreen(
    modifier: Modifier = Modifier,
    uiState: HomeworkInfoState,
    handleIntent: (HomeworkInfoIntent) -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
        ) {
            ImportanceAndStageSelector(
                modifier = Modifier
                    .padding(vertical = 4.dp),
                currentStageName = uiState.currentSelectedStage?.stageName ?: "",
                currentColor = uiState.currentColor,
                onColorSelectClick = {
                    handleIntent(HomeworkInfoIntent.OpenImportanceColorDialog)
                },
                onStageSelectClick = {
                    handleIntent(HomeworkInfoIntent.OpenStagePickerDialog)
                },
                currentStageColor = uiState.currentSelectedStage?.color ?: stageVariant8.toArgb()
            )
            Row(
                modifier = Modifier.padding(top = 12.dp),
            ) {
                Text(
                    style = MaterialTheme.typography.bodyLarge,
                    text = stringResource(R.string.subject_name),
                    color = Color.Gray
                )
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    text = uiState.subjectNameText,
                )
            }

            Row(
                modifier = Modifier.padding(top = 4.dp),
            ) {
                Text(
                    style = MaterialTheme.typography.bodyLarge,
                    text = stringResource(R.string.add_date),
                    color = Color.Gray
                )
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    text = uiState.addDateText,
                )
            }

            Row(
                modifier = Modifier.padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    style = MaterialTheme.typography.bodyLarge,
                    text = stringResource(R.string.finish_before),
                    color = Color.Gray
                )

                if (uiState.finishDateText.isNotBlank()) {
                    Text(
                        modifier = Modifier.padding(start = 4.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        text = uiState.finishDateText,
                    )
                } else {
                    TextButton(
                        onClick = {
                            handleIntent(HomeworkInfoIntent.SelectDateTime)
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
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.primary
            )
        }
        InfoContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            uiState = uiState,
            handleIntent = handleIntent,
        )
    }
}

@Composable
fun InfoContent(
    modifier: Modifier = Modifier,
    uiState: HomeworkInfoState,
    handleIntent: (HomeworkInfoIntent) -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        RichText(
            modifier = Modifier
                .clickable {
                    handleIntent(HomeworkInfoIntent.OnNameChangeClick)
                },
            state = uiState.nameRichTextState,
            style = TextStyle(fontSize = uiState.nameRichTextState.currentSpanStyle.fontSize)
        )
        if (uiState.descriptionRichTextState.annotatedString.text.isNotBlank()) {
            RichText(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .heightIn(min = 100.dp)
                    .clickable {
                        handleIntent(HomeworkInfoIntent.OnDescriptionChangeClick)
                    },
                state = uiState.descriptionRichTextState,
                style = TextStyle(fontSize = uiState.descriptionRichTextState.currentSpanStyle.fontSize)
            )
        } else {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 300.dp),
                onClick = {
                    handleIntent(HomeworkInfoIntent.OnDescriptionChangeClick)
                }
            )
            {

                Text(
                    modifier = Modifier.padding(top = 24.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    text = stringResource(R.string.add_description),
                    color = Color.Gray
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
    handleIntent: (HomeworkInfoIntent) -> Unit,
    focusRequester: FocusRequester
) {

    BackHandler {
        handleIntent(HomeworkInfoIntent.CloseNameChangeCard)
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
            if (imeInsetsToDp <= defaultBottomPadding) {
                bottomPadding = defaultBottomPadding
            } else {
                bottomPadding = with(density) { imeInsets.toDp() }
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
                            handleIntent(HomeworkInfoIntent.CloseNameChangeCard)
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
                    handleIntent(HomeworkInfoIntent.ToggleNameBold)
                },
                onItalicBtnClick = {
                    handleIntent(HomeworkInfoIntent.ToggleNameItalic)
                },
                onLineThroughBtnClick = {
                    handleIntent(HomeworkInfoIntent.ToggleNameLineThrough)
                },
                onUnderlinedBtnClick = {
                    handleIntent(HomeworkInfoIntent.ToggleNameUnderline)
                },
                onFontSizeChange = {
                    handleIntent(HomeworkInfoIntent.NameFontSizeChange(it))
                },
                onExtraOptionsBtnClick = {
                    handleIntent(HomeworkInfoIntent.ToggleNameExtraOptions)
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
    handleIntent: (HomeworkInfoIntent) -> Unit,
    focusRequester: FocusRequester
) {
    BackHandler {
        handleIntent(HomeworkInfoIntent.CloseDescriptionChangeCard)
    }
    Scaffold { paddingValues ->
        Column(
            modifier = modifier
                .imePadding()
                .padding(paddingValues),
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
                            handleIntent(HomeworkInfoIntent.CloseDescriptionChangeCard)
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
                    handleIntent(HomeworkInfoIntent.ToggleDescriptionBold)
                },
                onItalicBtnClick = {
                    handleIntent(HomeworkInfoIntent.ToggleDescriptionItalic)
                },
                onUnderlinedBtnClick = {
                    handleIntent(HomeworkInfoIntent.ToggleDescriptionUnderline)
                },
                onLineThroughBtnClick = {
                    handleIntent(HomeworkInfoIntent.ToggleDescriptionLineThrough)
                },
                onFontSizeChange = {
                    handleIntent(HomeworkInfoIntent.DescriptionFontSizeChange(it))
                },
                onExtraOptionsBtnClick = {
                    handleIntent(HomeworkInfoIntent.ToggleDescriptionExtraOptions)
                }
            )
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
                image = Icons.Default.MoreVert,
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

@Preview
@Composable
private fun Preview() {
    LocalHomeworkAndTaskManagerTheme {
        HomeworkInfoScreen(
            uiState = HomeworkInfoState(
                isLoading = false
            ),
            handleIntent = {}
        )
    }
}