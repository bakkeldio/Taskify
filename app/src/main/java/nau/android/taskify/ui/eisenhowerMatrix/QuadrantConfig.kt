package nau.android.taskify.ui.eisenhowerMatrix

import nau.android.taskify.ui.enums.Priority

data class QuadrantConfig(
    val allCategories: Boolean = true,
    val categories: List<Long> = emptyList(),
    val date: List<Date>,
    val priority: List<Priority>
)