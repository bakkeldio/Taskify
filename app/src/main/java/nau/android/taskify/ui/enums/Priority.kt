package nau.android.taskify.ui.enums

import androidx.compose.ui.graphics.Color

/**
 * Represents Priority in UI layer
 */
enum class Priority(val id: Int, val title: String, val color: Color) {
    HIGH(1, "High priority", Color(0xFFEE4B2B)),
    MEDIUM(2, "Medium priority", Color(0xFFF6BE00)),
    LOW(3, "Low priority", Color(0xFF0096FF)),
    NONE(4, "No priority", Color(0xFF696969))
}