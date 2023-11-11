package nau.android.taskify.ui.enums

/**
 * Represents Priority in UI layer
 */
enum class Priority(val id: Int, val title: String) {
    HIGH(1, "High priority"),
    MEDIUM(2, "Medium priority"),
    LOW(3, "Low priority"),
    NONE(4, "No priority")
}