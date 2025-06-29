package ru.pkstudio.localhomeworkandtaskmanager.core.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
/**
 * Default button for all purposes
 */
@Composable
fun DefaultButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    textStyle: TextStyle = MaterialTheme.typography.bodySmall,
    border: BorderStroke = BorderStroke(1.dp, color = MaterialTheme.colorScheme.primary)
) {
    OutlinedButton(
        modifier = modifier,
        colors = ButtonDefaults.outlinedButtonColors().copy(
            containerColor = Color.Transparent,
        ),
        border = border,
        shape = RoundedCornerShape(10.dp),
        onClick = onClick
    ) {
        Text(
            style = textStyle,
            text = text
        )
    }
}