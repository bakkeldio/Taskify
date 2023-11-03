package nau.android.taskify.data

import nau.android.taskify.ui.category.Category
import nau.android.taskify.ui.task.Task
import nau.android.taskify.ui.task.TaskPriority


fun generateTasks(): List<Task> {
    return listOf(
        Task(
            "Finish the project",
            "",
            Category("uid1", "Work"),
            "tomorrow",
            TaskPriority.Medium,
            false
        ),
        Task(
            "Make an appointment to the doctor",
            "",
            Category("uid2", "Health"),
            "today",
            TaskPriority.Medium,
            false
        ),
        Task(
            "Help my sister",
            "",
            Category("uid3", "Family"),
            "tomorrow",
            TaskPriority.Medium,
            false
        ),
        Task(
            "Hang out with friends",
            "",
            Category("uid4", "Friends"),
            "tomorrow",
            TaskPriority.High,
            false
        ),
        Task(
            "Finish watching the last season of Witcher",
            "",
            Category("uid5", "Sport"),
            "today",
            TaskPriority.High,
            false
        )
    )
}

fun generateCategories(): List<Category> {
    return listOf(
        Category("uid5", "Sport"),
        Category("uid4", "Friends"),
        Category("uid3", "Family"),
        Category("uid1", "Work"),
        Category("uid5", "Vacation"),
        Category("uid6", "Hobby")
    )
}