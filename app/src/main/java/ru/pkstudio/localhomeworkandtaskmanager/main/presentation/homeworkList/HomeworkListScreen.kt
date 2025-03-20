package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.LocalHomeworkAndTaskManagerTheme

@Composable
fun HomeworkListScreen(
    uiState: HomeworkListState,
    handleIntent: (HomeworkListIntent) -> Unit,
) {
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