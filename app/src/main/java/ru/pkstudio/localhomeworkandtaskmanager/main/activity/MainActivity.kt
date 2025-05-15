package ru.pkstudio.localhomeworkandtaskmanager.main.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.pkstudio.localhomeworkandtaskmanager.R
import ru.pkstudio.localhomeworkandtaskmanager.core.navigation.Navigator
import ru.pkstudio.localhomeworkandtaskmanager.core.navigation.SetupNavHost
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.LocalHomeworkAndTaskManagerTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity(){
    @Inject lateinit var navigator: Navigator
    private val activityViewModel: ActivityViewModel by viewModels()
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        var isReady = false
        super.onCreate(savedInstanceState)
        val context = this.applicationContext
        installSplashScreen().apply {
            setKeepOnScreenCondition{
                !isReady
            }
        }
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                lightScrim = context.getColor(R.color.black),
                darkScrim = context.getColor(R.color.white)
            ),
            navigationBarStyle = SystemBarStyle.dark(
                scrim = context.getColor(R.color.transparent),
            ),
        )
        setContent {
            val uiState = activityViewModel.uiState.collectAsStateWithLifecycle().value
            isReady = uiState.isReady
            val navController = rememberNavController()
            LocalHomeworkAndTaskManagerTheme(
                isSystemThemeEnabled = uiState.isSystemTheme,
                darkTheme = uiState.isDarkTheme,
                dynamicColor = uiState.isDynamicColor
            ) {
                SetupNavHost(
                    navController = navController,
                    navigator = navigator,
                    activityViewModel = activityViewModel
                )
            }
        }
    }
}