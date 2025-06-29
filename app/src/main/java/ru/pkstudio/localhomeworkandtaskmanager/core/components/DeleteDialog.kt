package ru.pkstudio.localhomeworkandtaskmanager.core.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.pkstudio.localhomeworkandtaskmanager.R

@Composable
fun DeleteDialog(
    modifier: Modifier = Modifier,
    title: String,
    comment: String,
    onDismissRequest: () -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit

) {
    AlertDialog(
        modifier = modifier,
        title = {
            Text(
                style = MaterialTheme.typography.headlineSmall,
                text = title
            )
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            DefaultButton(
                onClick = {
                    onConfirm()
                },
                text = stringResource(id = R.string.confirm_button),
                textStyle = MaterialTheme.typography.bodyMedium,
            )

        },
        dismissButton = {
            DefaultButton(
                modifier = Modifier.padding(end = 8.dp),
                onClick = {
                    onDismiss()
                },
                text = stringResource(id = R.string.dismiss_button),
                textStyle = MaterialTheme.typography.bodyMedium

            )
        },
        text = {
            Text(
                style = MaterialTheme.typography.bodyLarge,
                text = comment
            )
        }
    )
}