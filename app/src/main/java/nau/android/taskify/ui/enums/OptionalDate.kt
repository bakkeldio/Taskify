package nau.android.taskify.ui.enums

import java.util.Calendar

enum class OptionalDate(val title: String? = null) {
    Today("Today"), Tomorrow("Tomorrow"), ThreeDaysLater("Three days later"), NoSelection;

    companion object {
        fun getDate(optionalDate: OptionalDate): Calendar? {
            val calendar = Calendar.getInstance().clone() as Calendar
            return when (optionalDate) {
                Today -> calendar
                Tomorrow -> {
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                    calendar
                }

                ThreeDaysLater -> {
                    calendar.add(Calendar.DAY_OF_MONTH, 3)
                    calendar
                }

                NoSelection -> null
            }
        }

        fun fromCalendar(calendar: Calendar): OptionalDate {
            val today = Calendar.getInstance()
            val tomorrow = Calendar.getInstance()
            tomorrow.add(Calendar.DAY_OF_MONTH, 1)
            val threeDaysLater = Calendar.getInstance()
            threeDaysLater.add(Calendar.DAY_OF_MONTH, 3)
            val weekLater = Calendar.getInstance()
            weekLater.add(Calendar.DAY_OF_MONTH, 7)

            return when {
                isSameDay(calendar, today) -> Today
                isSameDay(calendar, tomorrow) -> Tomorrow
                isSameDay(calendar, threeDaysLater) -> ThreeDaysLater
                else -> NoSelection
            }
        }

        private fun isSameDay(calendar1: Calendar, calendar2: Calendar): Boolean {
            return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) && calendar1.get(
                Calendar.MONTH
            ) == calendar2.get(Calendar.MONTH) && calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(
                Calendar.DAY_OF_MONTH
            )
        }

    }
}