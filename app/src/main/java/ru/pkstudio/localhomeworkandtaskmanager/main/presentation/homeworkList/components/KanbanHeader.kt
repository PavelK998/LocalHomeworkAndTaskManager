package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList.uiModel.StageUiModel
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.LocalHomeworkAndTaskManagerTheme

@Composable
fun KanbanHeader(
    modifier: Modifier = Modifier,
    model: StageUiModel
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .weight(7f)
                    .padding(horizontal = 8.dp),
                text = model.stageName
            )
            Text(
                modifier = Modifier.weight(2f),
                textAlign = TextAlign.Center,
                text = model.itemsCount
            )
        }
        HorizontalDivider(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .padding(top = 4.dp, bottom = 8.dp),
            color = Color.Black
        )
    }
}


@Preview
@Composable
private fun KanbanHeaderPreview() {
    LocalHomeworkAndTaskManagerTheme {
        KanbanHeader(
            model = StageUiModel(
                stageName = "first",
                itemsCount = "20",
                id = 0L
            )
        )
    }
}