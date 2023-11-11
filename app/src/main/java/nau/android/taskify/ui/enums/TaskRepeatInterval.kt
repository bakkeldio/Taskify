package nau.android.taskify.ui.enums

enum class TaskRepeatInterval(val id: Int, val title: String = "") {
    NONE(0, ),
    HOURLY(1, "Hourly"),
    DAILY(2, "Daily"),
    WEEKLY(3, "Weekly"),
    MONTHLY(4, "Monthly"),
    YEARLY(5, "Yearly")
}