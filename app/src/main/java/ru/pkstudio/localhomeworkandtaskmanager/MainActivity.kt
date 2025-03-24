package ru.pkstudio.localhomeworkandtaskmanager

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.pkstudio.localhomeworkandtaskmanager.core.navigation.SetupNavHost
import ru.pkstudio.localhomeworkandtaskmanager.core.navigation.Navigator
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.LocalHomeworkAndTaskManagerTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var navigator: Navigator
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context = this.applicationContext
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                scrim = context.getColor(R.color.transparent),
            ),
            navigationBarStyle = SystemBarStyle.dark(
                scrim = context.getColor(R.color.transparent),
            ),
        )
        setContent {
            val navController = rememberNavController()
            LocalHomeworkAndTaskManagerTheme {
                SetupNavHost(
                    navController = navController,
                    navigator = navigator
                )
            }
        }
    }
}