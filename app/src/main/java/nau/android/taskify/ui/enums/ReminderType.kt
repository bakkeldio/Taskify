package nau.android.taskify.ui.enums

enum class ReminderType(val title: String) {
    OnTime("On time"), FiveMinutesBefore("5 minutes before"), TenMinutesBefore("10 minutes before"), FifteenMinutesBefore(
        "15 minutes before"
    ),
    ThirtyMinutesBefore("30 minutes before"), DayBefore("1 day before"), TwoDaysBefore("2 days before"), Customize(
        "Customize"
    );
}