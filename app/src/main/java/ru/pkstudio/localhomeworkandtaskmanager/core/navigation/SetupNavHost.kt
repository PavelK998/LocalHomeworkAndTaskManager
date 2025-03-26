package ru.pkstudio.localhomeworkandtaskmanager.core.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import ru.pkstudio.localhomeworkandtaskmanager.auth.AuthScreen
import ru.pkstudio.localhomeworkandtaskmanager.auth.AuthViewModel
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.addHomework.AddHomeworkScreen
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.addHomework.AddHomeworkViewModel
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.editStagesScreen.EditStagesScreen
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.editStagesScreen.EditStagesViewModel
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList.HomeworkListScreen
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.homeworkList.HomeworkListViewModel
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.settingsScreen.SettingsScreen
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.settingsScreen.SettingsViewModel
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.subjectList.SubjectListScreen
import ru.pkstudio.localhomeworkandtaskmanager.main.presentation.subjectList.SubjectListViewModel

@Composable
fun SetupNavHost(navController: NavHostController, navigator: Navigator) {

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
                val viewModel = hiltViewModel<SubjectListViewModel>()
                val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
                SubjectListScreen(
                    uiState = uiState,
                    handleIntent = viewModel::handleIntent
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
                SettingsScreen(
                    uiState = uiState,
                    handleIntent = viewModel::handleIntent
                )
            }

            composable<Destination.StageEditScreen> {
                val viewModel = hiltViewModel<EditStagesViewModel>()
                val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
                EditStagesScreen(
                    uiState = uiState,
                    handleIntent = viewModel::handleIntent
                )
            }
        }
    }
}