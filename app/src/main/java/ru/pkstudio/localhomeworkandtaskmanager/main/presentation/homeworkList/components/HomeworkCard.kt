package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import ru.pkstudio.localhomeworkandtaskmanager.R
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toStageNameInCardColor
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toTextColor
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList.uiModel.HomeworkUiModel
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.LocalHomeworkAndTaskManagerTheme

@Composable
fun HomeworkCard(
    modifier: Modifier = Modifier,
    homeworkUiModel: HomeworkUiModel,
    turnEditModeOn: () -> Unit,
    goToDetails: () -> Unit,
    onCheckCardClicked: (Boolean) -> Unit,
    onColorPaletteClicked: () -> Unit,
) {
    Log.d("fshhgfhgfdhfgdh", "HomeworkCard: ${homeworkUiModel.color}")
    Surface(
        color = Color(homeworkUiModel.color),
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .background(Color.Transparent)
            .pointerInput(homeworkUiModel.isChecked) {
                detectTapGestures(
                    onTap = {
                        if (homeworkUiModel.isCheckBoxVisible) {
                            onCheckCardClicked(homeworkUiModel.isChecked)
                        } else {
                            goToDetails()
                        }

                    },
                    onLongPress = {
                        if (!homeworkUiModel.isCheckBoxVisible) {
                            turnEditModeOn()
                        }

                    }
                )
            }
            .height(intrinsicSize = IntrinsicSize.Min)
    ) {
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
                Text(
                    text = stringResource(id = R.string.add_date),
                    color = Color(homeworkUiModel.color).toTextColor()
                )
                Text(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp),
                    style = MaterialTheme.typography.titleSmall,
                    text = homeworkUiModel.addDate,
                    textAlign = TextAlign.End,
                    color = Color(homeworkUiModel.color).toTextColor()
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
                    RichText(
                        modifier = Modifier
                            .padding(start = 8.dp, top = 8.dp),
                        state = rememberRichTextState().setHtml(homeworkUiModel.name),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color(homeworkUiModel.color).toTextColor()
                    )
                    RichText(
                        modifier = Modifier
                            .padding(vertical = 12.dp)
                            .padding(horizontal = 8.dp),
                        state = rememberRichTextState().setHtml(homeworkUiModel.description),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = Color(homeworkUiModel.color).toTextColor()
                    )
                }
                if (homeworkUiModel.isChecked) {
                    Icon(
                        modifier = Modifier.padding(end = 14.dp),
                        imageVector = Icons.Default.Check,
                        contentDescription = "check",
                        tint = Color(homeworkUiModel.color).toTextColor()
                    )
                } else {
                    IconButton(
                        onClick = {
                            onColorPaletteClicked()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "check",
                            tint = Color(homeworkUiModel.color).toTextColor()
                        )
                    }
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
                    color = Color(homeworkUiModel.color).toStageNameInCardColor(isSystemInDarkMode = isSystemInDarkTheme())
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
                isCheckBoxVisible = false,
                stageName = "",
                stageId = 0L,
                subjectId = 0L,
                color = -998220,
                importance = 1
            ),
            onCheckCardClicked = { _ -> },
            goToDetails = {},
            turnEditModeOn = {},
            onColorPaletteClicked = {}
        )
    }
}