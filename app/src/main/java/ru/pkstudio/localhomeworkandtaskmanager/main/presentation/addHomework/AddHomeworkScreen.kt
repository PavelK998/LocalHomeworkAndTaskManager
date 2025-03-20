package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.addHomework

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.LocalHomeworkAndTaskManagerTheme

@Composable
fun AddHomeworkScreen(
    uiState: AddHomeworkState,
    handleIntent: (AddHomeworkIntent) -> Unit
) {
    
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