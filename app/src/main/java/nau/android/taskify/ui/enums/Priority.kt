package nau.android.taskify.ui.enums

import androidx.compose.ui.graphics.Color

/**
 * Represents Priority in UI layer
 */
enum class Priority(val id: Int, val title: String, val color: Color) {
    HIGH(1, "High priority", Color(0xFFC41E3A)),
    MEDIUM(2, "Medium priority", Color(0xFFF6BE00)),
    LOW(3, "Low priority", Color(0xFF006AE2)),
    NONE(4, "No priority", Color(0xFF696969))
}