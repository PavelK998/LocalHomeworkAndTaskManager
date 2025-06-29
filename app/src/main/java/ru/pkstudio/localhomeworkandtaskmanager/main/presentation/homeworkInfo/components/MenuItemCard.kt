package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkInfo.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.LocalHomeworkAndTaskManagerTheme

@Composable
fun MenuItemCard(
    modifier: Modifier = Modifier,
    stageName: String,
    onCLick: () -> Unit,
    isActive: Boolean
) {
    val rotation = animateFloatAsState(
        targetValue = if (isActive) 180f else 0f,
        label = "rotation_arrow"
    )
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        onClick = {
            onCLick()
        }
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                style = MaterialTheme.typography.bodyLarge,
                text = stageName
            )

            Icon(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .graphicsLayer {
                        rotationZ = rotation.value
                    },
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = ""
            )

        }

    }
}

@Preview
@Composable
private fun MenuItemCardPreview() {
    LocalHomeworkAndTaskManagerTheme {
        MenuItemCard(
            stageName = "hello",
            onCLick = {},
            isActive = false
        )
    }
}
