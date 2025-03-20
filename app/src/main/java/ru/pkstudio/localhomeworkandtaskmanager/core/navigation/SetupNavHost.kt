package ru.pakarpichev.homeworktool.core.navigation

import ru.pkstudio.localhomeworkandtaskmanager.core.navigation.Navigator
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import ru.pkstudio.localhomeworkandtaskmanager.auth.AuthScreen
import ru.pkstudio.localhomeworkandtaskmanager.auth.AuthViewModel
import ru.pkstudio.localhomeworkandtaskmanager.core.navigation.Destination
import ru.pkstudio.localhomeworkandtaskmanager.core.navigation.NavigationAction
import ru.pkstudio.localhomeworkandtaskmanager.core.navigation.ObserveAsEvents
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
            composable<Destination.MainScreen> {
                val viewModel = hiltViewModel<SubjectListViewModel>()
                val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
                SubjectListScreen(
                    uiState = uiState,
                    handleIntent = viewModel::handleIntent
                )
            }
        }
    }
}