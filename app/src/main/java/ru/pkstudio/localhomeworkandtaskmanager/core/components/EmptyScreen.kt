package ru.pakarpichev.homeworktool.core.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.pakarpichev.homeworktool.R
import ru.pakarpichev.homeworktool.ui.theme.HomeworkToolTheme

@Composable
fun EmptyScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .weight(4f)
                .padding(horizontal = 24.dp)
                .alpha(0.7f),
            painter = painterResource(id = R.drawable.icon_no_content_dark),
            contentDescription = null
        )
//        if (isSystemInDarkTheme()){
//            Image(
//                modifier = Modifier
//                    .padding(horizontal = 24.dp),
//                painter = painterResource(id = R.drawable.icon_no_content_dark),
//                contentDescription = null
//            )
//        } else {
//            Image(
//                modifier = Modifier
//                    .padding(horizontal = 24.dp),
//                painter = painterResource(id = R.drawable.icon_no_content_light),
//                contentDescription = null
//            )
//        }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .weight(3f)
                .padding(24.dp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineMedium,
            text = stringResource(id = R.string.no_content)
        )
    }
}
@Preview()
@Composable
private fun EmptyScreenPreview() {
    HomeworkToolTheme {
        EmptyScreen(
            modifier = Modifier.background(
                MaterialTheme.colorScheme.surface
            )
        )
    }
}