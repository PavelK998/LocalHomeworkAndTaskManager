package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.subjectList.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.pkstudio.localhomeworkandtaskmanager.R
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.LocalHomeworkAndTaskManagerTheme

@Composable
fun SubjectCard(
    modifier: Modifier = Modifier,
    isEditModeEnabled: Boolean,
    navigateToHomeworkScreen: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onCommentChanged: (String) -> Unit,
    onConfirmChangesBtnCLicked: () -> Unit,
    onDiscardChangesBtnCLicked: () -> Unit,
    title: String,
    comment: String,
    editModeTitle: String,
    editModeComment: String
) {
    Card(
        modifier
            .fillMaxWidth()
            .clickable {
                navigateToHomeworkScreen()
            },
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface,
        )
    ) {
        if (isEditModeEnabled){
            EditMode(
                title = editModeTitle,
                comment = editModeComment,
                onTitleChanged = onTitleChanged,
                onCommentChanged = onCommentChanged,
                onConfirmChangesBtnCLicked = onConfirmChangesBtnCLicked,
                onDiscardChangesBtnCLicked = onDiscardChangesBtnCLicked
            )
        } else {
            InfoMode(title = title, comment = comment)
        }

    }
}

@Composable
private fun InfoMode(
    title: String,
    comment: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier
                .padding(
                    vertical = if (comment.isNotBlank()) 12.dp else 18.dp,
                    horizontal = 8.dp
                )
                .align(Alignment.CenterHorizontally),
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.W700,
        )
        if (comment.isNotBlank()){
            Text(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .padding(top = 4.dp, bottom = 12.dp)
                    .align(Alignment.Start),
                text = comment,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            )
        }

    }
}

@Composable
private fun EditMode(
    title: String,
    comment: String,
    onTitleChanged: (String) -> Unit,
    onCommentChanged: (String) -> Unit,
    onConfirmChangesBtnCLicked: () -> Unit,
    onDiscardChangesBtnCLicked: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier
                .weight(7f)
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors().copy(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                ),
                value = title,
                onValueChange = onTitleChanged,
                textStyle = MaterialTheme.typography.titleLarge,
                label = {
                    Text(stringResource(R.string.category_name))
                }

            )
            TextField(
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors().copy(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                ),
                value = comment,
                onValueChange = onCommentChanged,
                textStyle = MaterialTheme.typography.bodyLarge,
                label = {
                    Text(stringResource(R.string.comment))
                }
            )
        }
        IconButton(
            modifier = Modifier
                .weight(1f),
            onClick = {
                onConfirmChangesBtnCLicked()
        }) {
            Icon(
                modifier = Modifier.size(30.dp),
                tint = MaterialTheme.colorScheme.onSurface,
                imageVector = Icons.Default.Check,
                contentDescription = null
            )
        }
        IconButton(
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp),
            onClick = {
                onDiscardChangesBtnCLicked()
            }) {
            Icon(
                modifier = Modifier.size(30.dp),
                tint = MaterialTheme.colorScheme.onSurface,
                imageVector = Icons.Default.Close,
                contentDescription = null
            )
        }
    }
}


@Preview
@Composable
private fun SubjectCardPrev() {
    LocalHomeworkAndTaskManagerTheme {
        SubjectCard(
            onCommentChanged = {},
            onTitleChanged = {},
            editModeTitle = "Math",
            editModeComment = "Prepod Ivanov A U",
            navigateToHomeworkScreen = {},
            isEditModeEnabled = false,
            title = "Math",
            comment = "",
            onConfirmChangesBtnCLicked = {},
            onDiscardChangesBtnCLicked = {}
        )
    }
}