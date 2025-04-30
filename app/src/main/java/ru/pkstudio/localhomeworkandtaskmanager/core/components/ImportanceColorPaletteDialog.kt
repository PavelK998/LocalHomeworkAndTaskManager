package ru.pkstudio.localhomeworkandtaskmanager.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import ru.pkstudio.localhomeworkandtaskmanager.R
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.LocalHomeworkAndTaskManagerTheme
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.importance1
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.importance10
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.importance2
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.importance3
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.importance4
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.importance5
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.importance6
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.importance7
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.importance8
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.importance9


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportanceColorPaletteDialog(
    modifier: Modifier = Modifier,
    colorList: List<Color>,
    onSelectClick: (Color) -> Unit,
    onBtnDismissClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    BasicAlertDialog(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        onDismissRequest = {
            onDismiss()
        }
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Text(
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall,
                text = stringResource(R.string.selectColor)
            )
            Text(
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.titleMedium,
                text = stringResource(R.string.importance_info)
            )
            LazyRow(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(colorList) { _, color ->
                    Box(
                        modifier = Modifier
                            .background(color)
                            .size(50.dp)
                            .clickable {
                                onSelectClick(color)
                            }
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = {
                        onBtnDismissClick()
                    }
                ) {
                    Icon(
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(70.dp),
                        imageVector = Icons.Outlined.Cancel,
                        contentDescription = ""
                    )
                }
            }
        }


    }
}

@PreviewLightDark()
@Composable
private fun Preview() {
    LocalHomeworkAndTaskManagerTheme {
        ImportanceColorPaletteDialog(
            colorList = remember {
                mutableStateOf(
                    listOf(
                        importance1,
                        importance2,
                        importance3,
                        importance4,
                        importance5,
                        importance6,
                        importance7,
                        importance8,
                        importance9,
                        importance10,
                    )
                )
            }.value,
            onDismiss = {},
            onSelectClick = {},
            onBtnDismissClick = {}
        )
    }
}