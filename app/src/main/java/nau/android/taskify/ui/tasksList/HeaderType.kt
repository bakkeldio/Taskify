package nau.android.taskify.ui.tasksList

import nau.android.taskify.ui.enums.DateEnum

sealed class HeaderType(val title: String) {
    class Priority(name: String, val priorityNumber: Int) : HeaderType(name)
    class Date(dateEnum: DateEnum) : HeaderType(dateEnum.title)
    class Category(categoryName: String) : HeaderType(categoryName)
    object NoHeader : HeaderType("")
}