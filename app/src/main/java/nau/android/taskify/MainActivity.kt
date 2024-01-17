package nau.android.taskify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import dagger.hilt.android.AndroidEntryPoint
import nau.android.taskify.ui.DestinationNavArgs
import nau.android.taskify.googleAuth.GoogleAuthClient
import nau.android.taskify.ui.LoginDestination
import nau.android.taskify.ui.MainAppViewModel
import nau.android.taskify.ui.MainDestination
import nau.android.taskify.ui.alarm.permission.AlarmPermission
import nau.android.taskify.ui.calendar.TaskifyCalendar
import nau.android.taskify.ui.category.CategoriesList
import nau.android.taskify.ui.eisenhowerMatrix.EisenhowerMatrix
import nau.android.taskify.ui.login.LoginPage
import nau.android.taskify.ui.login.LoginWithEmailPassword
import nau.android.taskify.ui.login.PasswordRecovery
import nau.android.taskify.ui.login.SignUpPage
import nau.android.taskify.ui.model.Category
import nau.android.taskify.ui.model.Task
import nau.android.taskify.ui.settings.TaskifySettings
import nau.android.taskify.ui.task.TaskDetails
import nau.android.taskify.ui.task.TaskItemContent
import nau.android.taskify.ui.tasksList.AllTasksList
import nau.android.taskify.ui.tasksList.CategoryTasksList
import nau.android.taskify.ui.tasksList.QuadrantTasksList
import nau.android.taskify.ui.theme.TaskifyTheme
import javax.inject.Inject

val LocalSnackbarHost = compositionLocalOf {
    SnackbarHostState()
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var permissions: AlarmPermission

    @Inject
    lateinit var googleAuthClient: GoogleAuthClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            TaskifyTheme {
                CompositionLocalProvider(LocalTaskifyColors provides Colors()) {
                    MainPage(googleAuthClient, permissions)
                }
            }
        }
    }
}

@Composable
fun MainPage(
    authUiClient: GoogleAuthClient,
    alarmPermission: AlarmPermission,
    mainAppViewModel: MainAppViewModel = hiltViewModel()
) {

    val authState =
        mainAppViewModel.isUserSignedIn().collectAsStateWithLifecycle(initialValue = null)

    val navController = rememberNavController()

    var showBottomNavigation by rememberSaveable {
        mutableStateOf(true)
    }

    navController.addOnDestinationChangedListener { _, destination, _ ->
        showBottomNavigation = showBottomNavigationInMainDestinations(destination)
    }

    val navigateToTaskDetails: (Long) -> Unit = {
        navController.navigate("${MainDestination.TaskDetail}/$it")
    }

    val navigateUp: () -> Unit = {
        navController.popBackStack()
    }

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    CompositionLocalProvider(LocalSnackbarHost provides snackbarHostState) {

        Scaffold(bottomBar = {
            AnimatedVisibility(visible = showBottomNavigation) {
                TaskifyBottomNavigation(navController)
            }
        }, contentWindowInsets = WindowInsets(top = 0, bottom = 0), snackbarHost = {
            SnackbarHost(LocalSnackbarHost.current) {
                Snackbar(
                    snackbarData = it,
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    actionColor = MaterialTheme.colorScheme.primary
                )
            }
        }) { innerPaddings ->

            if (authState.value != null) {

                NavHost(
                    navController = navController,
                    startDestination = if (authState.value!!) MainDestination.ListOfTasks.route else "login",
                    modifier = Modifier.padding(innerPaddings)
                ) {


                    navigation(
                        startDestination = LoginDestination.WelcomeScreen.route,
                        route = "login"
                    ) {
                        composable(LoginDestination.WelcomeScreen.route) {

                            LoginPage(
                                googleAuthUiClient = authUiClient,
                                navigateToLoginWithEmailPassword = {
                                    navController.navigate(LoginDestination.SignInScreen.route)
                                })
                        }
                        composable(LoginDestination.SignInScreen.route) {
                            LoginWithEmailPassword(navigateToSignUpPage = {
                                navController.navigate(LoginDestination.SignUpScreen.route)
                            }, navigateUp = navigateUp, navigateToPasswordRecovery = {
                                navController.navigate(LoginDestination.PasswordRecoveryScreen.route)
                            })
                        }
                        composable(LoginDestination.SignUpScreen.route) {
                            SignUpPage(navigateUp)
                        }
                        composable(LoginDestination.PasswordRecoveryScreen.route) {
                            PasswordRecovery(navigateUp = navigateUp)
                        }
                        composable(LoginDestination.EmailVerificationScreen.route) {

                        }
                    }

                    composable(
                        MainDestination.ListOfTasks.route
                    ) {
                        AllTasksList(
                            title = MainDestination.ListOfTasks.title,
                            alarmPermission = alarmPermission,
                            onMainBottomBarVisibilityChanged = { isInMultiSelection ->
                                showBottomNavigation = !isInMultiSelection
                            },
                            navigateToTaskDetails = navigateToTaskDetails,
                            navigateUp = navigateUp
                        )
                    }

                    composable(
                        "${MainDestination.ListOfTasks.route}/${MainDestination.CategoryTasksList}/{${DestinationNavArgs.categoryId}}",
                        arguments = listOf(navArgument(DestinationNavArgs.categoryId) {
                            type = NavType.LongType
                        })
                    ) { backStackEntry ->
                        val categoryId =
                            backStackEntry.arguments?.getLong(DestinationNavArgs.categoryId)
                        CategoryTasksList(
                            categoryId = categoryId,
                            alarmPermission = alarmPermission,
                            navigateToTaskDetails = navigateToTaskDetails,
                            navigateUp = navigateUp
                        )
                    }

                    composable(
                        "${MainDestination.ListOfTasks.route}/${MainDestination.MatrixTasksList}/{${DestinationNavArgs.quadrantType}}",
                        arguments = listOf(navArgument(DestinationNavArgs.quadrantType) {
                            type = NavType.IntType
                        })
                    ) { backStackEntry ->
                        val quadrantId =
                            backStackEntry.arguments?.getInt(DestinationNavArgs.quadrantType)


                        QuadrantTasksList(
                            quadrantId = quadrantId,
                            alarmPermission = alarmPermission,
                            navigateToTaskDetails = navigateToTaskDetails,
                            navigateUp = navigateUp
                        )
                    }

                    composable(
                        "${MainDestination.TaskDetail}/{${DestinationNavArgs.taskId}}",
                        arguments = listOf(navArgument(DestinationNavArgs.taskId) {
                            type = NavType.LongType
                        })
                    ) { backStackEntry ->
                        val taskId = backStackEntry.arguments?.getLong(DestinationNavArgs.taskId)
                        TaskDetails(taskId, navigateUp = navigateUp)
                    }
                    composable(MainDestination.Categories.route) {
                        CategoriesList(MainDestination.Categories, navigateToCategoryTasksList = {
                            navController.navigate("${MainDestination.ListOfTasks.route}/${MainDestination.CategoryTasksList}/$it")
                        })
                    }
                    composable(MainDestination.EisenhowerMatrix.route) {
                        EisenhowerMatrix(
                            mainDestination = MainDestination.EisenhowerMatrix,
                            navigateToListDetails = {
                                navController.navigate("${MainDestination.ListOfTasks.route}/${MainDestination.MatrixTasksList}/${it.id}")
                            }, navigateToTaskDetails = {
                                navigateToTaskDetails(it.id)
                            })
                    }
                    composable(MainDestination.Calendar.route) {
                        TaskifyCalendar(
                            navigateToTaskDetails = navigateToTaskDetails
                        )
                    }
                    composable(MainDestination.AppSettings.route) { TaskifySettings() }
                }

            }
        }
    }
}

@Composable
fun TaskItemInMultiSelection(
    selected: Boolean,
    task: Task,
    category: Category? = null,
    showDetails: Boolean,
    onSelectChange: (Boolean) -> Unit
) {

    Card(
        modifier = Modifier
            .padding(start = 13.dp, end = 13.dp)
            .fillMaxWidth(), shape = RoundedCornerShape(10.dp)
    ) {
        TaskItemContent(
            true,
            selected,
            task = task,
            taskCategory = category,
            showDetails = showDetails,
            onSelectChange = onSelectChange
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskItem(
    task: Task,
    category: Category? = null,
    showDetails: Boolean,
    onComplete: () -> Unit,
    deleteTask: (Task) -> Unit,
    navigateToTaskDetails: (Long) -> Unit
) {

    var completed by remember {
        mutableStateOf(task.completed)
    }

    val dismissState = rememberDismissState(confirmValueChange = { dismissValue ->
        if (dismissValue == DismissValue.DismissedToStart) {
            deleteTask(task)
            true
        } else false
    })
    SwipeToDismiss(state = dismissState, background = {
        DismissBackground(dismissState = dismissState)
    }, dismissContent = {
        Card(
            modifier = Modifier
                .padding(start = 13.dp, end = 13.dp)
                .fillMaxWidth(), shape = RoundedCornerShape(10.dp)
        ) {
            TaskItemContent(
                false,
                completed,
                task,
                taskCategory = category,
                showDetails,
                {
                    completed = true
                    onComplete()
                },
                navigateToTaskDetails
            )
        }
    }, directions = setOf(DismissDirection.EndToStart))
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DismissBackground(dismissState: DismissState) {
    if (dismissState.dismissDirection == DismissDirection.EndToStart) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.error, shape = RoundedCornerShape(10.dp))
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_delete),
                contentDescription = stringResource(
                    id = R.string.delete_icon
                ),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(8.dp),
                tint = MaterialTheme.colorScheme.onError
            )
        }
    }
}


fun showBottomNavigationInMainDestinations(destination: NavDestination): Boolean {
    return destination.route == MainDestination.ListOfTasks.route ||
            destination.route == MainDestination.Categories.route ||
            destination.route == MainDestination.Calendar.route ||
            destination.route == MainDestination.EisenhowerMatrix.route ||
            destination.route == MainDestination.AppSettings.route

}

@Composable
fun FloatingActionButton(onClick: () -> Unit) {
    FloatingActionButton(onClick = {
        onClick()
    }) {
        Icon(imageVector = Icons.Filled.Add, contentDescription = "Floating action button")
    }
}

@Composable
fun EisenhowerMatrix(route: String) {
    Text(text = route)
}

@Composable
fun AppSettings(route: String) {
    Text(text = route)
}

@Composable
fun TaskifyBottomNavigation(navController: NavController) {

    NavigationBar(tonalElevation = 0.dp, containerColor = MaterialTheme.colorScheme.background) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        val items = listOf(
            MainDestination.ListOfTasks,
            MainDestination.Categories,
            MainDestination.EisenhowerMatrix,
            MainDestination.Calendar,
            MainDestination.AppSettings
        )

        items.forEach { item ->

            val isItemSelected = currentDestination?.hierarchy?.any {
                it.route == item.route
            } == true
            NavigationBarItem(selected = isItemSelected, onClick = {
                navController.navigate(item.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }, icon = {
                Icon(
                    painter = painterResource(id = if (isItemSelected) item.filledIcon else item.outlinedIcon),
                    contentDescription = null
                )
            }, colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.secondary
            )
            )
        }
    }
}

val LocalTaskifyColors = compositionLocalOf {
    Colors()
}

data class Colors(
    val completedTaskColor: Color = Color(0xFFA9A9A9)
)

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TaskifyTheme {
        Greeting("Android")
    }
}

