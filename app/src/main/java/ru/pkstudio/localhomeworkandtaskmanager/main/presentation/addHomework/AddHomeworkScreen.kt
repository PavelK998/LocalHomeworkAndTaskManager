package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.addHomework

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.pkstudio.localhomeworkandtaskmanager.R
import ru.pkstudio.localhomeworkandtaskmanager.core.components.DefaultTopAppBar
import ru.pkstudio.localhomeworkandtaskmanager.core.components.TopAppBarAction
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.LocalHomeworkAndTaskManagerTheme

@Composable
fun AddHomeworkScreen(
    uiState: AddHomeworkState,
    handleIntent: (AddHomeworkIntent) -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
               /// onEvent(AddHomeworkEvent.OnImagePicked(it))
            }
        }
    )

    val launcherForMultiplyImages = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(
            maxItems = 10
        ),
        onResult = { listUri ->
           // onEvent(AddHomeworkEvent.OnMultiplyImagePicked(listUri))
        }
    )
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
                        imageRes = R.drawable.icon_backspace,
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
            ) {
                TextField(
                    textStyle = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
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
                    value = uiState.title,
                    onValueChange = {
                        handleIntent.invoke(AddHomeworkIntent.OnTitleHomeworkChange(it))
                    },
                    label = {
                        Text(text = stringResource(id = R.string.add_homework_title_label))
                    }
                )
                TextField(
                    textStyle = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp),
                    value = uiState.description,
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
                        handleIntent.invoke(AddHomeworkIntent.OnDescriptionHomeworkChange(it))
                    },
                    label = {
                        Text(text = stringResource(id = R.string.add_homework_description_label))
                    }
                )
            }
//            LazyRow(
//                modifier = Modifier
//                    .padding(4.dp)
//                    .weight(1f)
//            ){
//                items(uiState.imagesList) { image ->
//                    ImageCard(
//                        modifier = Modifier
//                            .padding(horizontal = 4.dp)
//                            .size(90.dp),
//                        bitmap = image.second
//                    )
//                }
//            }
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