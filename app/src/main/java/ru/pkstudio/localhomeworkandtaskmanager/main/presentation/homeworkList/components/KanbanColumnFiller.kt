package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList.uiModel.HomeworkUiModel

@Composable
fun KanbanColumnFiller(
    modifier: Modifier = Modifier,
    model: HomeworkUiModel
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.background
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline
        ),
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp)
        ) {
            Column {
                Text(
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    text = model.name
                )
                Text(
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    text = model.description
                )
            }
        }
    }
}

@Preview
@Composable
private fun KanbanColumnFillerPreview() {
    KanbanColumnFiller(
        model = HomeworkUiModel(
            name = "12312312",
            description = "12312312312312",
            addDate = "",
            endDate = "",
            startDate = "",
            id = 0L,
            stageId = 0L,
            imageUrl = "",
            isCheckBoxVisible = false,
            isChecked = false,
            stageName = "",
            subjectId = 0L

        )
    )
}
