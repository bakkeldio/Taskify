package nau.android.taskify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
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
import dagger.hilt.android.AndroidEntryPoint
import nau.android.taskify.ui.Destination
import nau.android.taskify.ui.DestinationNavArgs
import nau.android.taskify.ui.alarm.permission.AlarmPermission
import nau.android.taskify.ui.calendar.TaskifyCalendar
import nau.android.taskify.ui.category.CategoriesList
import nau.android.taskify.ui.eisenhowerMatrix.EisenhowerMatrix
import nau.android.taskify.ui.model.Category
import nau.android.taskify.ui.model.Task
import nau.android.taskify.ui.task.TaskDetails
import nau.android.taskify.ui.task.TaskItemContent
import nau.android.taskify.ui.tasksList.AllTasksList
import nau.android.taskify.ui.tasksList.CategoryTasksList
import nau.android.taskify.ui.tasksList.QuadrantTasksList
import nau.android.taskify.ui.theme.TaskifyTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var permissions: AlarmPermission

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            TaskifyTheme {
                CompositionLocalProvider(LocalTaskifyColors provides Colors()) {
                    MainPage(permissions)
                }
            }
        }
    }
}

@Composable
fun MainPage(alarmPermission: AlarmPermission) {
    val navController = rememberNavController()

    var showBottomNavigation by rememberSaveable {
        mutableStateOf(true)
    }

    navController.addOnDestinationChangedListener { _, destination, _ ->
        showBottomNavigation = showBottomNavigationInMainDestinations(destination)
    }

    val navigateToTaskDetails: (Long) -> Unit = {
        navController.navigate("${Destination.TaskDetail}/$it")
    }

    val navigateUp: () -> Unit = {
        navController.popBackStack()
    }

    Scaffold(bottomBar = {
        AnimatedVisibility(visible = showBottomNavigation) {
            TaskifyBottomNavigation(navController)
        }
    }, contentWindowInsets = WindowInsets(top = 0, bottom = 0)) { innerPaddings ->

        NavHost(
            navController = navController,
            startDestination = Destination.ListOfTasks.route,
            modifier = Modifier.padding(innerPaddings)
        ) {

            composable(
                Destination.ListOfTasks.route
            ) {
                AllTasksList(
                    title = Destination.ListOfTasks.title,
                    alarmPermission = alarmPermission,
                    onMainBottomBarVisibilityChanged = { isInMultiSelection ->
                        showBottomNavigation = !isInMultiSelection
                    },
                    navigateToTaskDetails = navigateToTaskDetails,
                    navigateUp = navigateUp
                )
            }

            composable(
                "${Destination.ListOfTasks.route}/${Destination.CategoryTasksList}/{${DestinationNavArgs.categoryId}}",
                arguments = listOf(navArgument(DestinationNavArgs.categoryId) {
                    type = NavType.LongType
                })
            ) { backStackEntry ->
                val categoryId = backStackEntry.arguments?.getLong(DestinationNavArgs.categoryId)
                CategoryTasksList(
                    categoryId = categoryId,
                    alarmPermission = alarmPermission,
                    navigateToTaskDetails = navigateToTaskDetails,
                    navigateUp = navigateUp
                )
            }

            composable(
                "${Destination.ListOfTasks.route}/${Destination.MatrixTasksList}/{${DestinationNavArgs.quadrantType}}",
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
                "${Destination.TaskDetail}/{${DestinationNavArgs.taskId}}",
                arguments = listOf(navArgument(DestinationNavArgs.taskId) {
                    type = NavType.LongType
                })
            ) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getLong(DestinationNavArgs.taskId)
                TaskDetails(taskId, navigateUp = navigateUp)
            }
            composable(Destination.Categories.route) {
                CategoriesList(Destination.Categories, navigateToCategoryTasksList = {
                    navController.navigate("${Destination.ListOfTasks.route}/${Destination.CategoryTasksList}/$it")
                })
            }
            composable(Destination.EisenhowerMatrix.route) {
                EisenhowerMatrix(
                    destination = Destination.EisenhowerMatrix,
                    navigateToListDetails = {
                        navController.navigate("${Destination.ListOfTasks.route}/${Destination.MatrixTasksList}/${it.id}")
                    })
            }
            composable(Destination.Calendar.route) { TaskifyCalendar() }
            composable(Destination.AppSettings.route) { AppSettings(Destination.AppSettings.route) }
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

@Composable
fun TaskItem(
    task: Task,
    category: Category? = null,
    showDetails: Boolean,
    onComplete: () -> Unit,
    navigateToTaskDetails: (Long) -> Unit
) {

    var completed by remember {
        mutableStateOf(false)
    }
    Card(
        modifier = Modifier
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
}


fun showBottomNavigationInMainDestinations(destination: NavDestination): Boolean {
    return destination.route == Destination.ListOfTasks.route ||
            destination.route == Destination.Categories.route ||
            destination.route == Destination.Calendar.route ||
            destination.route == Destination.EisenhowerMatrix.route ||
            destination.route == Destination.AppSettings.route

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
            Destination.ListOfTasks,
            Destination.Categories,
            Destination.EisenhowerMatrix,
            Destination.Calendar,
            Destination.AppSettings
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

