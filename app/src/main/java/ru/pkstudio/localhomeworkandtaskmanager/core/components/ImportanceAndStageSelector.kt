package ru.pkstudio.localhomeworkandtaskmanager.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.pkstudio.localhomeworkandtaskmanager.R
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toImportance
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toTextColor
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.kanbanHeaderText

@Composable
fun ImportanceAndStageSelector(
    modifier: Modifier = Modifier,
    currentColor: Color,
    currentStageName: String,
    currentStageColor: Int,
    onColorSelectClick: () -> Unit,
    onStageSelectClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                style = MaterialTheme.typography.titleMedium,
                text = stringResource(R.string.importace)
            )
            Box(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(40.dp)
                    .background(currentColor)
                    .border(width = 1.dp, color = Color.Gray)
                    .clickable {
                        onColorSelectClick()
                    },
                contentAlignment = Alignment.Center
            ){
                Text(
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.W700,
                    text = currentColor.toImportance().toString(),
                    color = currentColor.toTextColor()
                )
            }
        }
        Row(
            modifier = Modifier.padding(start = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(

                style = MaterialTheme.typography.titleMedium,
                text = stringResource(R.string.stage)
            )
            Card(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .widthIn(min = 70.dp, max = 120.dp),
                onClick = {
                    onStageSelectClick()
                },
                colors = CardDefaults.cardColors().copy(
                    containerColor = Color(currentStageColor)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        modifier = Modifier
                            .padding(4.dp),
                        text = currentStageName,
                        textAlign = TextAlign.Center,
                        color = kanbanHeaderText
                    )
                }

            }
        }
    }
}