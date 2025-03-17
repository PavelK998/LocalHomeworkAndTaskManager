package ru.pakarpichev.homeworktool.core.presentation.components.kanban

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.pakarpichev.homeworktool.ui.theme.HomeworkToolTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun KanbanTestScreen(
    modifier: Modifier = Modifier,
    uiState: ViewModel.TestState,
    viewModel: ViewModel
) {

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {



        }

    }


}

@Composable
fun Filler(kanbanTestInfo: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, color = Color.Blue)
    ) {
        Text(
            modifier = Modifier
                .padding(14.dp),
            text = kanbanTestInfo,
            textAlign = TextAlign.Center
        )
    }

}
@Composable
fun Header(modifier: Modifier = Modifier, text: String) {
    Text(text = text)
}

@Composable
fun Footer(modifier: Modifier = Modifier) {
    Text(text = "FOOTER NEW")
}


@Preview
@Composable
private fun KanbanTestScreenPreview() {
    HomeworkToolTheme {
        KanbanTestScreen(uiState = ViewModel.TestState(), viewModel = ViewModel())
    }
}