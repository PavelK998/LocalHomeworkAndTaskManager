package ru.pakarpichev.homeworktool.core.navigation

import android.widget.Toast
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import ru.pakarpichev.homeworktool.auth.presentation.AuthScreen
import ru.pakarpichev.homeworktool.auth.presentation.CheckEmail.ConfirmEmailScreen
import ru.pakarpichev.homeworktool.auth.presentation.CheckEmail.ConfirmEmailViewModel
import ru.pakarpichev.homeworktool.auth.presentation.signIn.AuthUIAction
import ru.pakarpichev.homeworktool.auth.presentation.signIn.AuthViewModel
import ru.pakarpichev.homeworktool.auth.presentation.signUp.RegisterUIAction
import ru.pakarpichev.homeworktool.auth.presentation.signUp.RegistrationScreen
import ru.pakarpichev.homeworktool.auth.presentation.signUp.RegistrationViewModel
import ru.pakarpichev.homeworktool.auth.presentation.signUp.uiElements.RegisterUIActionNew
import ru.pakarpichev.homeworktool.core.presentation.components.kanban.KanbanTestScreen
import ru.pakarpichev.homeworktool.core.presentation.components.kanban.ViewModel
import ru.pakarpichev.homeworktool.main.presentation.addHomework.AddHomeworkScreen
import ru.pakarpichev.homeworktool.main.presentation.addHomework.AddHomeworkViewModel
import ru.pakarpichev.homeworktool.main.presentation.detailsHomework.DetailsHomeworkScreen
import ru.pakarpichev.homeworktool.main.presentation.detailsHomework.DetailsHomeworkViewModel
import ru.pakarpichev.homeworktool.main.presentation.homeworkList.HomeworkListScreen
import ru.pakarpichev.homeworktool.main.presentation.homeworkList.HomeworkListViewModel
import ru.pakarpichev.homeworktool.main.presentation.mainScreen.MainScreen
import ru.pakarpichev.homeworktool.main.presentation.mainScreen.MainViewModel

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
//            composable<Destination.KanbanScreen> {
//                val viewModel = hiltViewModel<ViewModel>()
//                val state = viewModel.uiState.collectAsStateWithLifecycle().value
//                KanbanTestScreen(uiState = state, viewModel = viewModel)
//            }
            composable<Destination.AuthScreen> {
                val viewModel = hiltViewModel<AuthViewModel>()
                val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
                val context = LocalContext.current
                val lifecycleOwner = LocalLifecycleOwner.current
                LaunchedEffect(key1 = lifecycleOwner.lifecycle) {
                    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.uiActionFlow.collect{ event ->
                            when(event) {
                                is AuthUIAction.ShowError -> {
                                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
                
                AuthScreen(
                    uiState = uiState,
                    onEvent = viewModel::onEvent
                )
            }
            composable<Destination.RegistrationScreen> {
                val viewModel = hiltViewModel<RegistrationViewModel>()
                val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
                val uiAction = viewModel.uiAction.collectAsStateWithLifecycle(initialValue = RegisterUIAction.ShowRegisterPage).value

                val context = LocalContext.current
                val lifecycleOwner = LocalLifecycleOwner.current
                LaunchedEffect(key1 = lifecycleOwner.lifecycle) {
                    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.uiActionFlow.collect{ event ->
                            when(event) {
                                is RegisterUIActionNew.ShowError -> {
                                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
                RegistrationScreen(
                    uiAction = uiAction,
                    uiState = uiState,
                    handleIntent = viewModel::handleIntent)
            }

            composable<Destination.ConfirmEmailScreen>{
                val viewModel = hiltViewModel<ConfirmEmailViewModel>()
                val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
                ConfirmEmailScreen(
                    uiState = uiState,
                    onEvent = viewModel::onEvent
                )
            }
        }

        navigation<Destination.MainGraph>(
            startDestination = Destination.MainScreen,
        ) {
            composable<Destination.MainScreen>(
                enterTransition = {
                    EnterTransition.None
                },
                exitTransition = {
                    ExitTransition.None
                }
            ) {
                val viewModel = hiltViewModel<MainViewModel>()
                val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
                MainScreen(
                    onEvent = viewModel::onEvent ,
                    uiState = uiState
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
                        subjectName = args.subjectNane,
                        subjectId = args.subjectId
                    )
                }
                HomeworkListScreen(
                    handleIntent = viewModel::handleIntent,
                    uiState = uiState
                )
            }

            composable<Destination.HomeworkAddScreen> { navBackStackEntry ->
                val args = navBackStackEntry.toRoute<Destination.HomeworkAddScreen>()
                val viewModel = hiltViewModel<AddHomeworkViewModel>()
                val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
                LaunchedEffect(key1 = true) {
                    viewModel.parseArguments(
                        subjectId = args.subjectId,
                        isThatFirstHomework = args.isThatFirstHomework
                    )
                }
                AddHomeworkScreen(
                    uiState = uiState,
                    onEvent = viewModel::handleIntent
                )
            }
            composable<Destination.DetailsHomeworkScreen> { navBackStackEntry ->
                val args = navBackStackEntry.toRoute<Destination.DetailsHomeworkScreen>()
                val viewModel = hiltViewModel<DetailsHomeworkViewModel>()
                val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
                LaunchedEffect(key1 = true) {
                    viewModel.parseArguments(
                        subjectId = args.subjectNane,
                        homeworkName = args.homeworkName
                    )
                }
                DetailsHomeworkScreen(
                    uiState = uiState
                )
            }
        }
    }
}