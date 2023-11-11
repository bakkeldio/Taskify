package nau.android.taskify.data.model


/**
 * Represents the interval between repeating intervals
 * @property id - the id of the interval
 */
enum class AlarmInterval(val id: Int) {
    NONE(0),
    HOURLY(1),
    DAILY(2),
    WEEKLY(3),
    MONTHLY(4),
    YEARLY(5)
}