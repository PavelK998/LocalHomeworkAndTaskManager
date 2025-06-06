package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import ru.pkstudio.localhomeworkandtaskmanager.R
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList.uiModel.HomeworkUiModel
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.LocalHomeworkAndTaskManagerTheme

@Composable
fun KanbanColumnFiller(
    modifier: Modifier = Modifier,
    model: HomeworkUiModel,
    onColorPaletteClicked: () -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.background
        ),
        border = BorderStroke(
            width = 3.dp,
            color = Color(model.color)
        ),
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.add_date),
                    )
                    Text(
                        modifier = Modifier
                            .padding(start = 2.dp),
                        style = MaterialTheme.typography.titleSmall,
                        text = model.addDate,
                        textAlign = TextAlign.End,
                    )
                }
                if (!model.isFinished && model.endDate.isNotBlank()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.finish_before),
                        )
                        Text(
                            modifier = Modifier
                                .padding(start = 2.dp),
                            style = MaterialTheme.typography.titleSmall,
                            text = model.endDate,
                            textAlign = TextAlign.End,
                        )
                    }
                }
                Row(
                    modifier = Modifier.padding(bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        RichText(
                            state = rememberRichTextState().setHtml(model.name),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        RichText(
                            modifier = Modifier.padding(vertical = 4.dp),
                            state = rememberRichTextState().setHtml(model.description),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    IconButton(
                        onClick = {
                            onColorPaletteClicked()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "change color",
                        )
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun KanbanColumnFillerPreview() {
    LocalHomeworkAndTaskManagerTheme {
        KanbanColumnFiller(
            model = HomeworkUiModel(
                name = "12312312",
                description = "12312312312312",
                addDate = "12312312",
                endDate = "12312312",
                startDate = "",
                id = 0L,
                stageId = 0L,
                imageNameList = emptyList(),
                isCheckBoxVisible = false,
                isChecked = false,
                stageName = "asdsad",
                subjectId = 0L,
                color = 0,
                importance = 1,
                isFinished = true

            ),
            onColorPaletteClicked = {}
        )
    }
}
