package nau.android.taskify.data.model

enum class ReminderType(val title: String) {
    ON_TIME("On time"),
    FIVE_MINUTES_BEFORE("5 minutes before"),
    TEN_MINUTES_BEFORE("10 minutes before"),
    FIFTEEN_MINUTES_BEFORE("15 minutes before"),
    THIRTY_MINUTES_BEFORE("30 minutes before"),
    DAY_BEFORE("1 day before"),
    TWO_DAYS_BEFORE("2 days before")
}