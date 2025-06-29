package ru.pkstudio.localhomeworkandtaskmanager.core.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import ru.pkstudio.localhomeworkandtaskmanager.R
import ru.pkstudio.localhomeworkandtaskmanager.core.util.SelectableDatesExceptPast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultDatePicker(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onConfirm: (Long?) -> Unit
) {

    val datePickerState = rememberDatePickerState(
        selectableDates = SelectableDatesExceptPast()

    )
    var isBtnConfirmEnabled by rememberSaveable {
        mutableStateOf(false)
    }
    LaunchedEffect(datePickerState.selectedDateMillis) {
        if (datePickerState.selectedDateMillis == null) {
            isBtnConfirmEnabled = false
        } else {
            isBtnConfirmEnabled = true
        }
    }
    DatePickerDialog(
        modifier = modifier,
        colors = DatePickerDefaults.colors().copy(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(datePickerState.selectedDateMillis)
                    onDismiss()
                },
                enabled = isBtnConfirmEnabled
            ) {
                Text(
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isBtnConfirmEnabled) {
                        MaterialTheme.colorScheme.onBackground
                    } else {
                        Color.Gray
                    },
                    text = stringResource(id = R.string.select)
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text(
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    text = stringResource(id = R.string.cancel)
                )
            }
        }
    ) {
        DatePicker(

            colors = DatePickerDefaults.colors().copy(
                containerColor = MaterialTheme.colorScheme.surface,
                selectedDayContainerColor = MaterialTheme.colorScheme.primary,
                selectedYearContainerColor = MaterialTheme.colorScheme.primary
            ),
            state = datePickerState
        )
    }
}