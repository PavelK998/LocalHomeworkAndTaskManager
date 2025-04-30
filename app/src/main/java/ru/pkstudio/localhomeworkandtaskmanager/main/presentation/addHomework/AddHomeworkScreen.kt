package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.addHomework

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults
import kotlinx.coroutines.delay
import ru.pkstudio.localhomeworkandtaskmanager.R
import ru.pkstudio.localhomeworkandtaskmanager.core.components.DefaultTopAppBar
import ru.pkstudio.localhomeworkandtaskmanager.core.components.EditTextPanel
import ru.pkstudio.localhomeworkandtaskmanager.core.components.TopAppBarAction
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.LocalHomeworkAndTaskManagerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHomeworkScreen(
    uiState: AddHomeworkState,
    handleIntent: (AddHomeworkIntent) -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                handleIntent(AddHomeworkIntent.OnImagePicked(it))
            }
        }
    )

    val launcherForMultiplyImages = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(
            maxItems = 10
        ),
        onResult = { listUri ->
            handleIntent(AddHomeworkIntent.OnMultiplyImagePicked(listUri))
        }
    )
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
            Modifier
                .fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.surface,
            topBar = {
                DefaultTopAppBar(
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
                                launcherForMultiplyImages.launch(
                                    PickVisualMediaRequest(
                                        mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .clickable {
                                handleIntent(AddHomeworkIntent.OnNameChangeClick)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                handleIntent(AddHomeworkIntent.OnNameChangeClick)
                            }
                        ) {
                            Icon(imageVector = Icons.Filled.Edit, contentDescription = "")
                        }
                        Text(
                            modifier = Modifier
                                .padding(start = 4.dp),
                            style = MaterialTheme.typography.titleLarge,
                            text = stringResource(R.string.name)
                        )
                    }
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


                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .padding(top = 12.dp)
                            .clickable {
                                handleIntent(AddHomeworkIntent.OnDescriptionChangeClick)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                handleIntent(AddHomeworkIntent.OnDescriptionChangeClick)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = ""
                            )
                        }
                        Text(
                            modifier = Modifier
                                .padding(start = 4.dp),
                            style = MaterialTheme.typography.titleLarge,
                            text = stringResource(R.string.description),
                        )
                    }
                    RichText(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .padding(top = 4.dp)
                            .clickable {
                                handleIntent(AddHomeworkIntent.OnNameChangeClick)
                            },
                        state = uiState.descriptionRichTextState,
                        style = TextStyle(fontSize = uiState.nameRichTextState.currentSpanStyle.fontSize)
                    )

                }
                AnimatedVisibility(uiState.imagesList.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier
                            .padding(4.dp)
                            .weight(1f),
                    ) {
                        itemsIndexed(
                            items = uiState.imagesList,
                            key = { _, item ->
                                item.first
                            }
                        ) { index, image ->
                            ImageCard(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .size(90.dp),
                                bitmap = image.second,
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
            enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
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
            enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
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
            if (imeInsetsToDp <= defaultBottomPadding) {
                bottomPadding = defaultBottomPadding
            } else {
                bottomPadding = with(density) {imeInsets.toDp()}
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
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
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
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
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

@Preview
@Composable
private fun Preview() {
    LocalHomeworkAndTaskManagerTheme {
        AddHomeworkScreen(
            uiState = AddHomeworkState(),
            handleIntent = {}
        )
    }
}