package ru.pkstudio.localhomeworkandtaskmanager.auth

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.pkstudio.localhomeworkandtaskmanager.R
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.LocalHomeworkAndTaskManagerTheme
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.success

@Composable
fun AuthScreen(
    uiState: AuthUiState,
    handleIntent: (AuthIntent) -> Unit
) {
    LaunchedEffect(key1 = uiState) {
        Log.d("sefsdfsdfdsf", "AuthScreen: ${uiState.text}")
    }
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.weight(0.8f),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 50.dp),
                    fontSize = 30.sp,
                    text = uiState.titleText
                )

                PinCode(
                    text = uiState.text,
                    emptyColor = MaterialTheme.colorScheme.primary,
                    filledColor = MaterialTheme.colorScheme.tertiary,
                    isError = uiState.isError,
                    isSuccess = uiState.isSuccess,
                )
            }
            Box(modifier = Modifier.weight(1.2f)) {
                Keyboard(
                    modifier = Modifier.padding(top = 50.dp),
                    onKeyboardClick = {
                        handleIntent(AuthIntent.OnKeyboardClicked(it))
                    }
                )
            }
        }
    }
}

@Composable
private fun PinCode(
    modifier: Modifier = Modifier,
    text: String,
    filledColor: Color,
    emptyColor: Color,
    isError: Boolean,
    isSuccess: Boolean
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            (1..4).forEach { index ->
                val color by animateColorAsState(
                    targetValue = if (text.length >= index) {
                        if (isError) {
                            MaterialTheme.colorScheme.error
                        } else if (isSuccess) {
                            success
                        } else {
                            filledColor
                        }
                    } else {
                        emptyColor
                    },
                    label = "boxColor"
                )
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(35.dp)
                        .background(color)
                )
            }
        }
        AnimatedVisibility(visible = isError) {
            Text(
                modifier = Modifier.padding(top = 12.dp),
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.error,
                text = stringResource(id = R.string.incorrect_password)
            )
        }
    }

}

@Composable
fun Keyboard(
    modifier: Modifier = Modifier,
    onKeyboardClick: (String) -> Unit
) {

    Column(
        modifier = modifier
    ) {
        listOf(1..3, 4..6, 7..9).forEach { range ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                range.forEach { index ->
                    TextButton(
                        colors = ButtonDefaults.buttonColors().copy(
                            contentColor = MaterialTheme.colorScheme.primary,
                            containerColor = Color.Transparent
                        ),
                        onClick = {
                            Log.d("saaasdsadasd", "Keyboard: ${index.toChar()}")
                            onKeyboardClick(index.toString())
                        }
                    ) {
                        Text(
                            fontSize = 40.sp,
                            text = index.toString()
                        )
                    }

                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                colors = ButtonDefaults.buttonColors().copy(
                    contentColor = MaterialTheme.colorScheme.primary,
                    containerColor = Color.Transparent
                ),
                onClick = {
                    onKeyboardClick("C")
                }
            ) {
                Text(
                    fontSize = 40.sp,
                    text = "C"
                )
            }
            TextButton(
                colors = ButtonDefaults.buttonColors().copy(
                    contentColor = MaterialTheme.colorScheme.primary,
                    containerColor = Color.Transparent
                ),
                onClick = {
                    onKeyboardClick("0")
                }
            ) {
                Text(
                    fontSize = 40.sp,
                    text = "0"
                )
            }
            IconButton(
                colors = IconButtonDefaults.iconButtonColors().copy(
                    contentColor = MaterialTheme.colorScheme.primary,
                    containerColor = Color.Transparent
                ),
                onClick = {
                    onKeyboardClick("-")
                }
            ) {
                Icon(
                    modifier = Modifier.size(40.dp),
                    painter = painterResource(id = R.drawable.icon_backspace),
                    contentDescription = ""
                )
            }

        }
    }
}

@Preview
@Composable
private fun AuthScreenPreview() {
    LocalHomeworkAndTaskManagerTheme {
        AuthScreen(
            uiState = AuthUiState(),
            handleIntent = {}
        )
    }
}