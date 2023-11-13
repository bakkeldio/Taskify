package nau.android.taskify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import nau.android.taskify.ui.MainDestination
import nau.android.taskify.ui.alarm.permission.AlarmPermission
import nau.android.taskify.ui.category.CategoriesList
import nau.android.taskify.ui.model.TaskWithCategory
import nau.android.taskify.ui.task.TaskDetails
import nau.android.taskify.ui.task.TaskItemDesign
import nau.android.taskify.ui.tasksList.ListOfTasks
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
                MainPage(permissions)
            }
        }
    }
}

@Composable
fun MainPage(alarmPermission: AlarmPermission) {
    val navController = rememberNavController()

    var showBottomNavigation by remember {
        mutableStateOf(true)
    }

    navController.addOnDestinationChangedListener { controller, destination, arguments ->
        showBottomNavigation = showBottomNavigationInMainDestinations(destination)
    }

    Scaffold(bottomBar = {
        if (showBottomNavigation) {
            TaskifyBottomNavigation(navController)
        }
    }, contentWindowInsets = WindowInsets(top = 0, bottom = 0)) { innerPaddings ->

        NavHost(
            navController = navController,
            startDestination = MainDestination.ListOfTasks.route,
            modifier = Modifier.padding(innerPaddings)
        ) {
            composable(MainDestination.ListOfTasks.route) {
                ListOfTasks(MainDestination.ListOfTasks, alarmPermission = alarmPermission) {
                    navController.navigate("task_details/$it")
                }
            }

            composable("task_details/{taskId}", arguments = listOf(navArgument("taskId") {
                type = NavType.LongType
            })) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getLong("taskId")
                TaskDetails(navController, taskId)
            }
            composable(MainDestination.Categories.route) { CategoriesList(MainDestination.Categories) }
            composable(MainDestination.EisenhowerMatrix.route) { EisenhowerMatrix(MainDestination.EisenhowerMatrix.route) }
            composable(MainDestination.Date.route) { TaskifyCalendar(MainDestination.Date.route) }
            composable(MainDestination.AppSettings.route) { AppSettings(MainDestination.AppSettings.route) }
        }

    }
}


@Composable
fun TaskItem(task: TaskWithCategory, showDetails: Boolean, navigateToTaskDetails: (Long) -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navigateToTaskDetails(task.task.id)
            }, shape = RoundedCornerShape(10.dp)
    ) {
        TaskItemDesign(task, showDetails)
    }
}


fun showBottomNavigationInMainDestinations(destination: NavDestination): Boolean {
    return destination.route == MainDestination.ListOfTasks.route ||
            destination.route == MainDestination.Categories.route ||
            destination.route == MainDestination.Date.route ||
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
fun TaskifyCalendar(route: String) {
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
            MainDestination.Date,
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

