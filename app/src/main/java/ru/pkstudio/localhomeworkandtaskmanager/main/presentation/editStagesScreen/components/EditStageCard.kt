package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.editStagesScreen.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.StageModel
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.LocalHomeworkAndTaskManagerTheme

@Composable
fun EditStageCard(
    modifier: Modifier = Modifier,
    stage: StageModel,
    onTextChanged: (text: String) -> Unit,
    onAddBtnClick: () -> Unit,
    onDeleteBtnClick: () -> Unit,
) {
    var text by rememberSaveable {
        mutableStateOf(stage.stageName)
    }
    Card(modifier = modifier) {
        Row(
            modifier = Modifier.padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                modifier = Modifier
                    .weight(7f)
                    .padding(horizontal = 8.dp),
                colors = TextFieldDefaults.colors().copy(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                value = text,
                onValueChange = {
                    text = it
                    onTextChanged(it)
                }
            )
            IconButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    onAddBtnClick()
                }
            ) {
                Icon(
                    tint = MaterialTheme.colorScheme.primary,
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = ""
                )
            }

            IconButton(
                modifier = Modifier.padding(horizontal = 8.dp).weight(1f),
                onClick = {
                    onDeleteBtnClick()
                }
            ) {
                Icon(
                    tint = MaterialTheme.colorScheme.primary,
                    imageVector = Icons.Default.Delete,
                    contentDescription = ""
                )
            }

        }

    }
}


@Preview
@Composable
private fun EditStageCardPreview() {
    LocalHomeworkAndTaskManagerTheme {
        EditStageCard(
            stage = StageModel(
                id = 10,
                stageName = "First Stage",
                position = 0
            ),
            onTextChanged = {},
            onAddBtnClick = {},
            onDeleteBtnClick = {}
        )
    }
}