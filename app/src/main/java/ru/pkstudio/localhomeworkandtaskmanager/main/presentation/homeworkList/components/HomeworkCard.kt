package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.pkstudio.localhomeworkandtaskmanager.R
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
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
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
        if (homeworkUiModel.isCheckBoxVisible) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(homeworkUiModel.isChecked) {
                        detectTapGestures(
                            onTap = {
                                onCheckCardClicked(homeworkUiModel.isChecked)
                            },
//                            onLongPress = {
//                                turnEditModeOn()
//                            }
                        )
                    },
                color = Color.Transparent
            ) {}
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(id = R.string.add_date))
                Text(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp),
                    style = MaterialTheme.typography.titleSmall,
                    text = homeworkUiModel.addDate,
                    textAlign = TextAlign.End
                )
            }

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
                if (homeworkUiModel.isChecked) {
                    Icon(
                        modifier = Modifier.padding(end = 14.dp),
                        imageVector = Icons.Default.Check,
                        contentDescription = "check",
                        tint = MaterialTheme.colorScheme.tertiary
                    )

//                    Checkbox(
//                        colors = CheckboxDefaults.colors().copy(
//                            uncheckedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
//                            checkedBorderColor = MaterialTheme.colorScheme.secondaryContainer
//                        ),
//                        checked = homeworkUiModel.isChecked,
//                        onCheckedChange = {
//                            onCheckCardClicked(
//                                homeworkUiModel.isChecked
//                            )
//                        }
//                    )
                }
            }
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    modifier = Modifier
                        .padding(bottom = 16.dp, end = 24.dp),
                    style = MaterialTheme.typography.titleSmall,
                    text = homeworkUiModel.stageName,
                    textAlign = TextAlign.End,
                    color = MaterialTheme.colorScheme.primaryContainer
                )

            }
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
                addDate = "12.10.2024",
                name = "Сделать доклад",
                description = "Важный доклад по важной теме",
                startDate = "",
                endDate = "",
                imageUrl = "",
                isChecked = false,
                isCheckBoxVisible = true,
                stageName = "",
                stageId = 0L,
                subjectId = 0L
            ),
            onCheckCardClicked = { _ -> },
            goToDetails = {},
            turnEditModeOn = {},
        )
    }
}