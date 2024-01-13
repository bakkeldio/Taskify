package nau.android.taskify.ui.eisenhowerMatrix

import nau.android.taskify.R

enum class Date(val title: Int) {
    ALL(R.string.all),
    NO_DATE(R.string.no_date),
    OVERDUE(R.string.overdue),
    REPEAT(R.string.repeat),
    TODAY(R.string.today),
    TOMORROW(R.string.tomorrow),
    TWO_DAYS_LATER(R.string.two_days_later),
    THIS_WEEK(R.string.this_week),
    NEXT_WEEK(R.string.next_week),
    THIS_MONTH(R.string.this_month)
}