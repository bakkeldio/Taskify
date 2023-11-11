package nau.android.taskify.ui.enums


/**
 * Represents type of the Date headers
 */
enum class DateEnum(val title: String, val id: Int) {
    OVERDUE("Overdue", 1),
    TODAY("Today", 2),
    TOMORROW("Tomorrow", 3),
    NEXT_SEVEN_DAYS("Next 7 days", 4),
    LATER("Later", 5),
    NO_DATE("No date", 6)
}