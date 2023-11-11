package nau.android.taskify.data.database.typeConverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import nau.android.taskify.data.model.ReminderType
import java.lang.reflect.Type
import java.util.Calendar
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestampToDate(value: Long?): Calendar? {
        return value?.let {
            val calendar = Calendar.getInstance()
            calendar.time = Date(it)
            calendar
        }
    }

    @TypeConverter
    fun dateToTimestamp(calendar: Calendar?): Long? {
        return calendar?.time?.time
    }


    @TypeConverter
    fun fromString(value: String?): List<ReminderType>? {
        if (value == null) {
            return null
        }

        val listType: Type = object : TypeToken<List<ReminderType>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun toString(list: List<ReminderType>?): String? {
        if (list == null) {
            return null
        }

        val gson = Gson()
        return gson.toJson(list)
    }
}