package ru.pkstudio.localhomeworkandtaskmanager.auth

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.Player
import androidx.media3.ui.PlayerView
import ru.pkstudio.localhomeworkandtaskmanager.R
import ru.pkstudio.localhomeworkandtaskmanager.auth.utils.AuthAction
import ru.pkstudio.localhomeworkandtaskmanager.core.components.DefaultButton
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.LocalHomeworkAndTaskManagerTheme
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.success

@Composable
fun AuthScreen(
    uiState: AuthUiState,
    handleIntent: (AuthIntent) -> Unit,
    player: Player?
) {
    BackHandler {
        handleIntent(AuthIntent.OnBackBtnClicked)
    }
    var lifecycle by remember {
        mutableStateOf(Lifecycle.Event.ON_CREATE)
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            lifecycle = event
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    if (uiState.isFirstLaunch) {
        if (player != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                AndroidView(
                    factory = { context ->
                        PlayerView(context).apply {
                            this.player = player
                            useController = false
                        }
                    },
                    update = {
                        when (lifecycle) {
                            Lifecycle.Event.ON_PAUSE -> {
                                it.onPause()
                                it.player?.pause()
                            }

                            Lifecycle.Event.ON_RESUME -> {
                                it.onResume()
                                it.player?.play()
                            }

                            else -> {}
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            when (uiState.currentAuthAction) {
                AuthAction.SET_PIN -> {
                    SetPinCode(
                        modifier = Modifier.padding(paddingValues),
                        uiState = uiState,
                        handleIntent = handleIntent
                    )
                }

                AuthAction.SELECT_THEME -> {
                    SelectTheme(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        uiState = uiState,
                        setTheme = { themeId, isSystemInDarkMode ->
                            handleIntent(
                                AuthIntent.SetThemeId(
                                    themeId = themeId,
                                    isSystemInDarkMode = isSystemInDarkMode
                                )
                            )
                        },
                        onSelectBtnClick = { handleIntent(AuthIntent.OnThemeSelected) }
                    )
                }

                AuthAction.SELECT_USAGE -> {
                    SelectUsage(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(horizontal = 16.dp),
                        setDiary = {
                            handleIntent(AuthIntent.SetDiaryUsage)
                        },
                        setTaskTracker = {
                            handleIntent(AuthIntent.SetTaskTrackerUsage)
                        }
                    )
                }

                else -> {}
            }
        }
    }
}

@Composable
fun SelectUsage(
    modifier: Modifier = Modifier,
    setDiary: () -> Unit,
    setTaskTracker: () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            style = MaterialTheme.typography.titleLarge,
            text = stringResource(R.string.auth_usage_title)
        )
        Text(
            modifier = Modifier.padding(top = 12.dp),
            style = MaterialTheme.typography.titleMedium,
            text = stringResource(R.string.auth_usage_sub_title)
        )
        Text(
            modifier = Modifier.padding(top = 4.dp),
            style = MaterialTheme.typography.bodyLarge,
            text = stringResource(R.string.auth_usage_parentheses)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            DefaultButton(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                onClick = {
                    setTaskTracker()
                },
                text = stringResource(R.string.auth_usage_btn_task_tracker)
            )
            DefaultButton(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                onClick = {
                    setDiary()
                },
                text = stringResource(R.string.auth_usage_btn_diary)
            )
        }

    }
}

@Composable
fun SelectTheme(
    modifier: Modifier = Modifier,
    uiState: AuthUiState,
    setTheme: (themeId: Int, isSystemInDarkMode: Boolean) -> Unit,
    onSelectBtnClick: () -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = {
            uiState.listUiThemes.size
        }
    )
    val isDarkTheme = isSystemInDarkTheme()
    LaunchedEffect(pagerState.currentPage) {
        setTheme(pagerState.currentPage, isDarkTheme)
    }
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        HorizontalPager(
            state = pagerState
        ) { page ->
            when (uiState.listUiThemes[page]) {
                stringResource(R.string.enable_system_theme) -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            style = MaterialTheme.typography.bodyLarge,
                            text = stringResource(R.string.enable_system_theme)
                        )
                    }
                }

                stringResource(R.string.enable_light_theme) -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            style = MaterialTheme.typography.bodyLarge,
                            text = stringResource(R.string.enable_light_theme)
                        )
                    }
                }

                stringResource(R.string.enable_dark_theme) -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            style = MaterialTheme.typography.bodyLarge,
                            text = stringResource(R.string.enable_dark_theme)
                        )
                    }
                }
            }

        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            Text(
                style = MaterialTheme.typography.displaySmall,
                text = stringResource(R.string.auth_theme_title)
            )

            Text(
                modifier = Modifier.padding(top = 4.dp),
                style = MaterialTheme.typography.titleSmall,
                text = stringResource(R.string.auth_theme_hint)
            )
            Spacer(modifier = Modifier.weight(0.5f))
            Box(
                modifier = Modifier
                    .weight(0.2f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    onClick = {
                        onSelectBtnClick()
                    },
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    )
                ) {
                    Text(
                        style = MaterialTheme.typography.titleLarge,
                        text = stringResource(R.string.select)
                    )
                }
            }

        }
    }
}

@Composable
private fun SetPinCode(
    modifier: Modifier,
    uiState: AuthUiState,
    handleIntent: (AuthIntent) -> Unit
) {
    Column(
        modifier = modifier
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
            uiState = AuthUiState(
                currentAuthAction = AuthAction.SELECT_THEME
            ),
            handleIntent = {},
            player = null
        )
    }
}