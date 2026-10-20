package chingiz.simulationform.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    ),
)

/**
 * Font sizes reflecting the form hierarchy:
 * pages > sections (shrinking with nesting) > questions.
 */
object HierarchyTypography {
    private const val SECTION_BASE = 22
    private const val SECTION_STEP = 2
    private const val SECTION_MIN = 16

    val page: TextUnit = 26.sp
    val question: TextUnit = 14.sp

    /** [level] is 0 for a top-level section and grows by one for each nested section. */
    fun section(level: Int): TextUnit =
        (SECTION_BASE - SECTION_STEP * level).coerceAtLeast(SECTION_MIN).sp

    fun pageStyle() = TextStyle(fontSize = page, fontWeight = FontWeight.Bold, lineHeight = page * 1.25f)

    fun sectionStyle(level: Int): TextStyle {
        val size = section(level)
        return TextStyle(fontSize = size, fontWeight = FontWeight.SemiBold, lineHeight = size * 1.25f)
    }

    fun questionStyle() = TextStyle(fontSize = question, lineHeight = question * 1.4f)
}
