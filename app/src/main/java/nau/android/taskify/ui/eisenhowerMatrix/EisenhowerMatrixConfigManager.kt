package nau.android.taskify.ui.eisenhowerMatrix

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import nau.android.taskify.ui.enums.Priority
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class EisenhowerMatrixConfigManager @Inject constructor(@ApplicationContext private val context: Context) {

    companion object {
        private const val CONFIG_NAME = "EISENHOWER_MATRIX_CONFIG"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE)

    private fun getDefaultConfig(eisenhowerMatrixQuadrant: EisenhowerMatrixQuadrant): QuadrantConfig {
        return when (eisenhowerMatrixQuadrant) {
            EisenhowerMatrixQuadrant.IMPORTANT_URGENT -> QuadrantConfig(
                date = listOf(Date.OVERDUE, Date.TODAY, Date.TOMORROW),
                priority = listOf(Priority.HIGH, Priority.MEDIUM)
            )

            EisenhowerMatrixQuadrant.NOT_URGENT_IMPORTANT -> QuadrantConfig(
                date = listOf(Date.NO_DATE, Date.TWO_DAYS_LATER),
                priority = listOf(Priority.HIGH, Priority.MEDIUM)
            )

            EisenhowerMatrixQuadrant.URGENT_UNIMPORTANT -> QuadrantConfig(
                date = listOf(Date.OVERDUE, Date.TODAY, Date.TOMORROW),
                priority = listOf(Priority.LOW, Priority.NONE)
            )

            EisenhowerMatrixQuadrant.NOT_URGENT_UNIMPORTANT -> QuadrantConfig(
                date = listOf(Date.NO_DATE, Date.TWO_DAYS_LATER),
                priority = listOf(Priority.NONE, Priority.LOW)
            )
        }
    }

    private fun getMatrixQuadrantConfig(eisenhowerMatrixQuadrant: EisenhowerMatrixQuadrant): QuadrantConfig {
        val config = sharedPreferences.getString("${eisenhowerMatrixQuadrant.id}", null)
        return if (config != null) {
            Gson().fromJson(config, QuadrantConfig::class.java)
        } else {
            getDefaultConfig(eisenhowerMatrixQuadrant)
        }
    }


    fun getAllConfigurations(): Map<EisenhowerMatrixQuadrant, QuadrantConfig> {
        val configurations = mutableMapOf<EisenhowerMatrixQuadrant, QuadrantConfig>()
        configurations[EisenhowerMatrixQuadrant.IMPORTANT_URGENT] =
            getMatrixQuadrantConfig(EisenhowerMatrixQuadrant.IMPORTANT_URGENT)
        configurations[EisenhowerMatrixQuadrant.NOT_URGENT_IMPORTANT] =
            getMatrixQuadrantConfig(EisenhowerMatrixQuadrant.NOT_URGENT_IMPORTANT)
        configurations[EisenhowerMatrixQuadrant.URGENT_UNIMPORTANT] =
            getMatrixQuadrantConfig(EisenhowerMatrixQuadrant.URGENT_UNIMPORTANT)
        configurations[EisenhowerMatrixQuadrant.NOT_URGENT_UNIMPORTANT] =
            getMatrixQuadrantConfig(EisenhowerMatrixQuadrant.NOT_URGENT_UNIMPORTANT)
        return configurations
    }

    fun saveConfigurations(quadrantId: Int, config: QuadrantConfig) {
        val configurationJson = Gson().toJson(config)
        sharedPreferences.edit {
            putString("$quadrantId", configurationJson)
        }
    }

    fun removeConfiguration(quadrantId: Int) {
        sharedPreferences.edit {
            remove("$quadrantId")
        }
    }
}
