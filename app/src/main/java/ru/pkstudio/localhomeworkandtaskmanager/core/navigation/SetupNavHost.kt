package ru.pkstudio.localhomeworkandtaskmanager.core.navigation

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Process
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.documentfile.provider.DocumentFile
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import ru.pkstudio.localhomeworkandtaskmanager.R
import ru.pkstudio.localhomeworkandtaskmanager.auth.AuthIntent
import ru.pkstudio.localhomeworkandtaskmanager.auth.AuthScreen
import ru.pkstudio.localhomeworkandtaskmanager.auth.AuthUiAction
import ru.pkstudio.localhomeworkandtaskmanager.auth.AuthViewModel
import ru.pkstudio.localhomeworkandtaskmanager.core.util.ObserveAsActions
import ru.pkstudio.localhomeworkandtaskmanager.main.activity.ActivityViewModel
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.addHomework.AddHomeworkIntent
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.addHomework.AddHomeworkScreen
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.addHomework.AddHomeworkUIAction
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.addHomework.AddHomeworkViewModel
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.editStagesScreen.EditStageUiAction
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.editStagesScreen.EditStagesScreen
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.editStagesScreen.EditStagesViewModel
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkInfo.HomeworkInfoIntent
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkInfo.HomeworkInfoScreen
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkInfo.HomeworkInfoUiAction
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkInfo.HomeworkInfoViewModel
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList.HomeworkListScreen
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList.HomeworkListViewModel
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.settingsScreen.SettingsIntent
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.settingsScreen.SettingsScreen
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.settingsScreen.SettingsUIAction
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.settingsScreen.SettingsViewModel
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.subjectList.SubjectListScreen
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.subjectList.SubjectListUiAction
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.subjectList.SubjectListViewModel

@SuppressLint("SourceLockedOrientationActivity")
@Composable
fun SetupNavHost(
    navController: NavHostController,
    navigator: Navigator,
    activityViewModel: ActivityViewModel
) {

    ObserveAsEvents(flow = navigator.navigationActions) { navigationAction ->
        when (navigationAction) {
            is NavigationAction.Navigate -> {
                navController.navigate(
                    navigationAction.destination
                ) {
                    navigationAction.navOptions(this)
                }
            }

            is NavigationAction.NavigateUp -> {
                navController.navigateUp()
            }
        }

    }

    NavHost(
        navController = navController,
        startDestination = navigator.startDestination,
    ) {
        navigation<Destination.AuthGraph>(
            startDestination = Destination.AuthScreen
        ) {
            composable<Destination.AuthScreen> {
                val viewModel = hiltViewModel<AuthViewModel>()
                val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
                val context = LocalContext.current
                val configuration = LocalConfiguration.current
                val isLandscape by remember(configuration) {
                    mutableStateOf(configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
                }
                val screenWidthPx by remember {
                    mutableIntStateOf(configuration.screenWidthDp * (configuration.densityDpi / 160))
                }
                ObserveAsActions(flow = viewModel.uiAction) { action ->
                    when (action) {
                        is AuthUiAction.SetDarkTheme -> {
                            activityViewModel.toggleDarkThemeFromAuth()
                        }

                        is AuthUiAction.SetLightTheme -> {
                            activityViewModel.toggleLightThemeFromAuth()
                        }

                        is AuthUiAction.SetSystemTheme -> {
                            activityViewModel.toggleSystemThemeFromAuth()
                        }

                        is AuthUiAction.ToggleDynamicColors -> {
                            activityViewModel.toggleDynamicColors()
                        }

                        is AuthUiAction.FinishActivity -> {
                            (context as? Activity)?.finish()
                        }

                        is AuthUiAction.GetInitialData -> {
                            viewModel.handleIntent(AuthIntent.GetInitialData(configuration.orientation))
                        }

                        is AuthUiAction.SetUnspecifiedOrientation -> {
                            val activity = context as ComponentActivity
                            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                        }
                    }
                }
                AuthScreen(
                    uiState = uiState,
                    handleIntent = viewModel::handleIntent,
                    player = viewModel.player,
                    isLandscape = isLandscape,
                    screenWidthPx = screenWidthPx
                )
            }
        }

        navigation<Destination.MainGraph>(
            startDestination = Destination.MainScreen
        ) {
            composable<Destination.MainScreen>(
                enterTransition = {
                    EnterTransition.None
                },
                exitTransition = {
                    ExitTransition.None
                }
            ) {
                val context = LocalContext.current
                val viewModel = hiltViewModel<SubjectListViewModel>()
                val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
                ObserveAsActions(flow = viewModel.uiAction) { action ->
                    when (action) {
                        is SubjectListUiAction.ShowErrorMessage -> {
                            Toast.makeText(context, action.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                SubjectListScreen(
                    uiState = uiState,
                    handleIntent = viewModel::handleIntent,
                )
            }

            composable<Destination.HomeworkListScreen>(
                enterTransition = {
                    EnterTransition.None
                },
                exitTransition = {
                    ExitTransition.None
                }
            ) { navBackStackEntry ->
                val args = navBackStackEntry.toRoute<Destination.HomeworkListScreen>()
                val viewModel = hiltViewModel<HomeworkListViewModel>()
                val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
                LaunchedEffect(key1 = true) {
                    viewModel.parseArguments(
                        subjectId = args.subjectId
                    )
                }
                HomeworkListScreen(
                    uiState = uiState,
                    handleIntent = viewModel::handleIntent
                )
            }

            composable<Destination.HomeworkAddScreen>(
                enterTransition = {
                    EnterTransition.None
                },
                exitTransition = {
                    ExitTransition.None
                }
            ) { navBackStackEntry ->
                val context = LocalContext.current
                val args = navBackStackEntry.toRoute<Destination.HomeworkAddScreen>()
                val viewModel = hiltViewModel<AddHomeworkViewModel>()
                val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
                val launchSelectFilePath = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.OpenDocumentTree(),
                    onResult = { uri ->
                        val appName = context.getString(R.string.app_name)
                        uri?.let {
                            try {
                                val documentFile =  DocumentFile.fromTreeUri(context, it)
                                val appFolder = documentFile?.createDirectory(appName)
                                if (appFolder != null) {
                                    val takeFlags =
                                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                    context.contentResolver.takePersistableUriPermission(uri, takeFlags)
                                    viewModel.handleIntent(
                                        AddHomeworkIntent.OnFileExportPathSelected(
                                            appFolder.uri
                                        )
                                    )
                                }

                            } catch (e:Exception) {

                            }
                        }
                    }
                )
                val launcherForMultiplyImages = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.PickMultipleVisualMedia(
                        maxItems = 10
                    ),
                    onResult = { listUri ->
                        viewModel.handleIntent(AddHomeworkIntent.OnMultiplyImagePicked(listUri))
                    }
                )
                ObserveAsActions(flow = viewModel.uiAction) { action ->
                    when (action) {
                        is AddHomeworkUIAction.ShowError -> {
                            Toast.makeText(context, action.text, Toast.LENGTH_SHORT).show()
                        }

                        is AddHomeworkUIAction.LaunchPhotoPicker -> {
                            Log.d("dghdfgdfgdfgdfg", "isPhotoPickerAvailable: ${ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable(context)}")
                            launcherForMultiplyImages.launch(
                                PickVisualMediaRequest(
                                    mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        }

                        is AddHomeworkUIAction.LaunchPathSelectorForSaveImages -> {
                            launchSelectFilePath.launch(null)
                        }
                    }
                }
                LaunchedEffect(key1 = true) {
                    viewModel.parseArguments(
                        subjectId = args.subjectId
                    )
                }

                AddHomeworkScreen(
                    uiState = uiState,
                    handleIntent = viewModel::handleIntent
                )
            }

            composable<Destination.DetailsHomeworkScreen>(
                enterTransition = {
                    EnterTransition.None
                },
                exitTransition = {
                    ExitTransition.None
                }
            ) { navBackStackEntry ->
                val args = navBackStackEntry.toRoute<Destination.DetailsHomeworkScreen>()
                val viewModel = hiltViewModel<HomeworkInfoViewModel>()
                val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
                val context = LocalContext.current
                LaunchedEffect(key1 = true) {
                    viewModel.parseArguments(
                        homeworkId = args.homeworkId,
                        subjectId = args.subjectId
                    )
                }
                val launchSelectFilePath = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.OpenDocumentTree(),
                    onResult = { uri ->
                        val appName = context.getString(R.string.app_name)
                        uri?.let {
                            try {
                                val documentFile =  DocumentFile.fromTreeUri(context, it)
                                val appFolder = documentFile?.createDirectory(appName)
                                if (appFolder != null) {
                                    val takeFlags =
                                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                    context.contentResolver.takePersistableUriPermission(uri, takeFlags)
                                    viewModel.handleIntent(
                                        HomeworkInfoIntent.OnFileExportPathSelected(
                                            appFolder.uri
                                        )
                                    )
                                }

                            } catch (e:Exception) {

                            }
                        }
                    }
                )
                val launcherForMultiplyImages = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.PickMultipleVisualMedia(
                        maxItems = 10
                    ),
                    onResult = { listUri ->
                        viewModel.handleIntent(HomeworkInfoIntent.OnMultiplyImagePicked(listUri))
                    }
                )
                ObserveAsActions(flow = viewModel.uiAction) { action ->
                    when (action) {
                        is HomeworkInfoUiAction.ShowError -> {
                            Toast.makeText(context, action.text, Toast.LENGTH_SHORT).show()
                        }

                        is HomeworkInfoUiAction.LaunchPhotoPicker -> {
                            launcherForMultiplyImages.launch(
                                PickVisualMediaRequest(
                                    mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        }

                        is HomeworkInfoUiAction.LaunchPathSelectorForSaveImages -> {
                            launchSelectFilePath.launch(null)
                        }
                    }
                }
                HomeworkInfoScreen(
                    uiState = uiState,
                    handleIntent = viewModel::handleIntent
                )
            }

            composable<Destination.SettingsScreen>(
                enterTransition = {
                    EnterTransition.None
                },
                exitTransition = {
                    ExitTransition.None
                }
            ) {
                val viewModel = hiltViewModel<SettingsViewModel>()
                val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
                val context = LocalContext.current
                val launchSelectFilePath = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.OpenDocumentTree(),
                    onResult = { uri ->
                        val appName = context.getString(R.string.app_name)
                        uri?.let {
                            try {
                                val documentFile =  DocumentFile.fromTreeUri(context, it)
                                val appFolder = documentFile?.createDirectory(appName)
                                if (appFolder != null) {
                                    val takeFlags =
                                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                    context.contentResolver.takePersistableUriPermission(uri, takeFlags)
                                    viewModel.handleIntent(
                                        SettingsIntent.OnFileExportPathSelected(
                                            appFolder.uri
                                        )
                                    )
                                }

                            } catch (e:Exception) {

                            }
                        }
                    }
                )
                val launchOpenDocument = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult(),
                    onResult = { result ->
                        if (result.resultCode == RESULT_OK) {
                            result.data?.data?.let { uri ->
                                Log.d("sadasdsadas", "SetupNavHost: import uri $uri")
                                viewModel.handleIntent(
                                    SettingsIntent.OnFileImportPathSelected(
                                        uri
                                    )
                                )
                            }
                        }
                    }
                )
                ObserveAsActions(flow = viewModel.uiAction) { action ->
                    when (action) {
                        is SettingsUIAction.ShowError -> {
                            Toast.makeText(context, action.message, Toast.LENGTH_SHORT).show()
                        }

                        is SettingsUIAction.SetDarkTheme -> {
                            activityViewModel.toggleDarkTheme()
                        }

                        is SettingsUIAction.SetDynamicColors -> {
                            activityViewModel.toggleDynamicColors()
                        }

                        is SettingsUIAction.SetLightTheme -> {
                            activityViewModel.toggleLightTheme()
                        }

                        is SettingsUIAction.SetSystemTheme -> {
                            activityViewModel.toggleSystemTheme(action.isSystemInDarkMode)
                        }

                        is SettingsUIAction.OpenDocumentTree -> {
                            launchSelectFilePath.launch(null)
                        }

                        is SettingsUIAction.SelectDatabaseFile -> {
                            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                                addCategory(Intent.CATEGORY_OPENABLE)
                                type = "*/*"
                            }
                            launchOpenDocument.launch(intent)
                        }

                        is SettingsUIAction.RestartApp -> {
                            val intent =
                                context.packageManager.getLaunchIntentForPackage(context.packageName)
                            intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            context.startActivity(intent)
                            Process.killProcess(Process.myPid())
                        }
                    }
                }
                SettingsScreen(
                    uiState = uiState,
                    handleIntent = viewModel::handleIntent
                )
            }

            composable<Destination.StageEditScreen>(
                enterTransition = {
                    EnterTransition.None
                },
                exitTransition = {
                    ExitTransition.None
                }
            ) {
                val viewModel = hiltViewModel<EditStagesViewModel>()
                val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
                val context = LocalContext.current
                ObserveAsActions(flow = viewModel.uiAction) { action ->
                    when (action) {
                        is EditStageUiAction.ShowErrorMessage -> {
                            Toast.makeText(context, action.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                EditStagesScreen(
                    uiState = uiState,
                    handleIntent = viewModel::handleIntent
                )
            }
        }
    }
}