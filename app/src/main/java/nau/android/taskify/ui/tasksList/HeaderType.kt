package nau.android.taskify.ui.tasksList

sealed class HeaderType(val title: String) {
    class Priority(name: String, val priorityNumber: Int) : HeaderType(name)
    class Date(name: String, val date: String) : HeaderType(name)
    class Category(categoryName: String) : HeaderType(categoryName)
    object NoHeader : HeaderType("")
}