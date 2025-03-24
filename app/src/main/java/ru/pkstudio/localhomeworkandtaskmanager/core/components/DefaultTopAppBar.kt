package ru.pakarpichev.homeworktool.core.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.pkstudio.localhomeworkandtaskmanager.core.components.TopAppBarAction
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.LocalHomeworkAndTaskManagerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultTopAppBar(
    modifier: Modifier = Modifier,
    title: String,
    navigationIcon: ImageVector,
    navigationAction: () -> Unit,
    actions: List<TopAppBarAction>,
    dropDownMenu: @Composable () -> Unit = {}
    ) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors().copy(

        ),
        title = {
            Text(
                modifier = Modifier
                    .padding(start = 12.dp),
                text = title
            )
        },
        navigationIcon = {
            IconButton(
                modifier = modifier
                    .padding(start = 8.dp),
                onClick = {
                    navigationAction.invoke()
                }
            ) {
                Icon(
                    imageVector = navigationIcon,
                    contentDescription = null,
                )
            }
        },
        actions = {
            actions.forEach { action ->
                IconButton(
                    modifier = modifier
                        .padding(end = 8.dp),
                    onClick = {
                        action.action.invoke()
                    }
                ) {
                    if (action.image != null) {
                        Icon(
                            imageVector = action.image,
                            contentDescription = action.contentDescription,
                            tint = action.tint
                        )
                    } else if (action.imageRes != 0){
                        Icon(
                            painter = painterResource(id = action.imageRes),
                            contentDescription = action.contentDescription)
                    }

                }
            }
            dropDownMenu.invoke()
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun DefaultTopAppBarPreview() {
    LocalHomeworkAndTaskManagerTheme {
        DefaultTopAppBar(
            title = "Top bar",
            navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
            navigationAction = {},
            actions = listOf(
                TopAppBarAction(
                    image = Icons.Default.Delete,
                    contentDescription = "",
                    action = {},
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        )
    }
}