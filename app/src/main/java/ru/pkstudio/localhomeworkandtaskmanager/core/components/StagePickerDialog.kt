package ru.pkstudio.localhomeworkandtaskmanager.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.pkstudio.localhomeworkandtaskmanager.R
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.StageModel
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.LocalHomeworkAndTaskManagerTheme
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.kanbanHeaderText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StagePickerDialog(
    modifier: Modifier = Modifier,
    stageList: List<StageModel>,
    onSelectStageClick: (Int) -> Unit,
    onDismiss: () -> Unit,
    onCreateStageBtnClick: () -> Unit,
) {
    BasicAlertDialog(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.background),
        onDismissRequest = {
            onDismiss()
        }
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
        ) {
            Text(
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall,
                text = stringResource(R.string.selectStage)
            )

            LazyColumn(
                modifier = Modifier.padding(top = 24.dp).heightIn(max = 300.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                itemsIndexed(stageList) { index, model ->
                    Card(
                        colors = CardDefaults.cardColors().copy(
                            containerColor = Color(model.color)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onSelectStageClick(index)
                        }
                    ) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            text = model.stageName,
                            textAlign = TextAlign.Center,
                            color = kanbanHeaderText
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                OutlinedButton(
                    onClick = {
                        onCreateStageBtnClick()
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = ""
                        )
                        Text(stringResource(R.string.add_stage))
                    }
                }
            }
        }


    }
}

@Preview
@Composable
private fun Preview() {
    LocalHomeworkAndTaskManagerTheme {
        StagePickerDialog(
            stageList = listOf(
                StageModel(
                    id = 0,
                    color = -5234535,
                    stageName = "toDo",
                    position = 0,
                    isFinishStage = false
                )
            ),
            onDismiss = {},
            onSelectStageClick = {},
            onCreateStageBtnClick = {}
        )
    }
}