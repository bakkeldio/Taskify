package nau.android.taskify.data

import nau.android.taskify.ui.GroupingType
import nau.android.taskify.ui.HeaderType
import nau.android.taskify.ui.SortingType
import nau.android.taskify.ui.Task


class FakeTasksData(private var listOfTasks: List<Task>) {

    fun getTasks(): List<Task> {
        return listOfTasks
    }

    fun setTasks(newList: List<Task>) {
        this.listOfTasks = newList
    }


    fun getTasksInGroups(
        groupingType: GroupingType
    ): Map<HeaderType, List<Task>> {

        return when (groupingType) {
            GroupingType.Priority -> listOfTasks.groupBy { task ->
                task.taskPriority
            }.mapKeys {
                HeaderType.Priority(it.key.name, it.key.priorityNumber)
            }

            GroupingType.Category -> listOfTasks.groupBy { task ->
                task.category
            }.mapKeys {
                HeaderType.Category(it.key?.name ?: "No Category")
            }

            GroupingType.Date -> listOfTasks.groupBy { task ->
               task.taskDate
            }.mapKeys {
                HeaderType.Date(it.key, it.key)
            }

            GroupingType.None -> mapOf(Pair(HeaderType.NoHeader, listOfTasks))
        }


    }

    fun getSortedList(sortingType: SortingType): List<Task> {
        return listOfTasks.sortedBy { task ->
            when (sortingType) {
                SortingType.Title -> task.name
                SortingType.Date -> task.taskDate
                SortingType.Priority -> task.taskPriority.name
            }
        }
    }

    fun getSortingTypes(): List<SortingType> {
        return listOf(SortingType.Date, SortingType.Title, SortingType.Priority)
    }

    fun getGroupingTypes(): List<GroupingType> {
        return listOf(
            GroupingType.Priority, GroupingType.Date, GroupingType.Category, GroupingType.None
        )
    }


}