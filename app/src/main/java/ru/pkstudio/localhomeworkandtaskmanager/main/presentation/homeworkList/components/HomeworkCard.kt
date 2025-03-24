package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList.uiModel.HomeworkUiModel
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.LocalHomeworkAndTaskManagerTheme

@Composable
fun HomeworkCard(
    modifier: Modifier = Modifier,
    homeworkUiModel: HomeworkUiModel,
    turnEditModeOn: () -> Unit,
    goToDetails: () -> Unit,
    onCheckCardClicked: (Boolean) -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .background(Color.Transparent)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        goToDetails()
                    },
                    onLongPress = {
                        turnEditModeOn()
                    }
                )
            }
            .height(intrinsicSize = IntrinsicSize.Min)
    ) {
        if (homeworkUiModel.isCheckBoxVisible){
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(homeworkUiModel.isChecked) {
                        detectTapGestures(
                            onTap = {
                                onCheckCardClicked(homeworkUiModel.isChecked)
                            },
                            onLongPress = {
                                turnEditModeOn()
                            }
                        )
                    },
                color = Color.Transparent
            ){}
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp, top = 4.dp),
                style = MaterialTheme.typography.titleSmall,
                text = homeworkUiModel.addDate,
                textAlign = TextAlign.End
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        modifier = Modifier
                            .padding(start = 8.dp, top = 8.dp),
                        style = MaterialTheme.typography.titleLarge,
                        text = homeworkUiModel.name
                    )
                    Text(
                        modifier = Modifier
                            .padding(vertical = 12.dp)
                            .padding(horizontal = 8.dp),
                        text = homeworkUiModel.description,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (homeworkUiModel.isCheckBoxVisible) {
                    Checkbox(
                        colors = CheckboxDefaults.colors().copy(
                            uncheckedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                            checkedBorderColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        checked = homeworkUiModel.isChecked,
                        onCheckedChange = {
                            onCheckCardClicked(
                                homeworkUiModel.isChecked
                            )
                        }
                    )
                }
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp, top = 4.dp),
                style = MaterialTheme.typography.titleSmall,
                text = "${homeworkUiModel.startDate} - ${homeworkUiModel.endDate}",
                textAlign = TextAlign.End
            )
        }
    }
}

@Preview
@Composable
private fun HomeworkCardPreview() {
    LocalHomeworkAndTaskManagerTheme {
        HomeworkCard(
            homeworkUiModel = HomeworkUiModel(
                id = 0L,
                addDate ="12.10.2024",
                name = "Сделать доклад",
                description = "Важный доклад по важной теме",
                startDate = "",
                endDate = "",
                imageUrl = "",
                isChecked = false,
                isCheckBoxVisible = false,
                stageName = "",
                stageId = 0L
            ),
            onCheckCardClicked = { _ -> },
            goToDetails = {},
            turnEditModeOn = {},
        )
    }
}