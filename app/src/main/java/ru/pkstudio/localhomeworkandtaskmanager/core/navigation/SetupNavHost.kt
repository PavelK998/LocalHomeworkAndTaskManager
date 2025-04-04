package ru.pkstudio.localhomeworkandtaskmanager.core.navigation

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Process
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import kotlinx.coroutines.launch
import ru.pkstudio.localhomeworkandtaskmanager.auth.AuthScreen
import ru.pkstudio.localhomeworkandtaskmanager.auth.AuthViewModel
import ru.pkstudio.localhomeworkandtaskmanager.core.util.ObserveAsActions
import ru.pkstudio.localhomeworkandtaskmanager.main.activity.ActivityViewModel
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.addHomework.AddHomeworkScreen
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.addHomework.AddHomeworkViewModel
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.editStagesScreen.EditStageUiAction
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.editStagesScreen.EditStagesScreen
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.editStagesScreen.EditStagesViewModel
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList.HomeworkListScreen
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList.HomeworkListViewModel
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.settingsScreen.SettingsIntent
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.settingsScreen.SettingsScreen
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.settingsScreen.SettingsViewModel
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.subjectList.SubjectListIntent
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.subjectList.SubjectListScreen
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.subjectList.SubjectListUiAction
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.subjectList.SubjectListViewModel

@Composable
fun SetupNavHost(
    navController: NavHostController,
    navigator: Navigator,
    activityViewModel: ActivityViewModel
) {

    ObserveAsEvents(flow = navigator.navigationActions) { navigationAction ->
        when(navigationAction) {
            is NavigationAction.Navigate -> {
                navController.navigate(
                    navigationAction.destination
                ){
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
                AuthScreen(
                    uiState = uiState,
                    handleIntent = viewModel::handleIntent
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
                val coroutineScope = rememberCoroutineScope()
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val viewModel = hiltViewModel<SubjectListViewModel>()
                val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
                val launchSelectFilePath = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult(),
                    onResult = { result ->
                        if (result.resultCode == RESULT_OK) {
                            result.data?.data?.let { uri ->
                                Log.d("sadasdsadas", "SetupNavHost: export uri $uri")
                                val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                context.contentResolver.takePersistableUriPermission(uri, takeFlags)
                                viewModel.handleIntent(SubjectListIntent.OnFileExportPathSelected(uri))
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
                                viewModel.handleIntent(SubjectListIntent.OnFileImportPathSelected(uri))
                            }
                        }
                    }
                )
                ObserveAsActions(flow = viewModel.uiAction) { action ->
                    when (action) {
                        is SubjectListUiAction.CloseDrawer -> {
                            coroutineScope.launch {
                                drawerState.close()
                            }
                        }

                        is SubjectListUiAction.OpenDrawer -> {
                            coroutineScope.launch {
                                drawerState.open()
                            }
                        }

                        is SubjectListUiAction.ShowErrorMessage -> {
                            Toast.makeText(context, action.message, Toast.LENGTH_SHORT).show()
                        }

                        is SubjectListUiAction.OpenDocumentTree -> {
                            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                            launchSelectFilePath.launch(intent)
                        }

                        is SubjectListUiAction.SelectDatabaseFile -> {
                            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                                addCategory(Intent.CATEGORY_OPENABLE)
                                type = "*/*"
                            }
                            launchOpenDocument.launch(intent)
                        }

                        is SubjectListUiAction.RestartApp -> {
                            val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
                            intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            context.startActivity(intent)
                            Process.killProcess(Process.myPid())
                        }
                    }
                }
                SubjectListScreen(
                    uiState = uiState,
                    handleIntent = viewModel::handleIntent,
                    drawerState = drawerState,
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

            composable<Destination.HomeworkAddScreen> { navBackStackEntry ->
                val args = navBackStackEntry.toRoute<Destination.HomeworkAddScreen>()
                val viewModel = hiltViewModel<AddHomeworkViewModel>()
                val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
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

            composable<Destination.SettingsScreen> {
                val viewModel = hiltViewModel<SettingsViewModel>()
                val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
                fun handleIntent(intent: SettingsIntent) {
                    when(intent) {
                        is SettingsIntent.SetDarkTheme -> {
                            activityViewModel.toggleDarkTheme()
                            viewModel.handleIntent(SettingsIntent.SetDarkTheme)
                        }

                        is SettingsIntent.SetDynamicColors -> {
                            activityViewModel.toggleDynamicColors()
                            viewModel.handleIntent(SettingsIntent.SetDynamicColors)
                        }

                        is SettingsIntent.SetLightTheme -> {
                            activityViewModel.toggleLightTheme()
                            viewModel.handleIntent(SettingsIntent.SetLightTheme)
                        }

                        is SettingsIntent.SetSystemTheme -> {
                            activityViewModel.toggleSystemTheme(intent.isSystemInDarkMode)
                            viewModel.handleIntent(SettingsIntent.SetSystemTheme(intent.isSystemInDarkMode))
                        }

                        else -> viewModel.handleIntent(intent)
                    }
                }
                SettingsScreen(
                    uiState = uiState,
                    handleIntent = ::handleIntent
                )
            }

            composable<Destination.StageEditScreen> {
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