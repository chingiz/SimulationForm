package chingiz.simulationform.presentation.form.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import chingiz.simulationform.ui.theme.PageAccent
import chingiz.simulationform.ui.theme.SectionAccent

private val IndentStep: Dp = 16.dp
private val GuideOffset = 8.dp
private val PageGuideWidth = 3.dp
private val SectionGuideWidth = 1.5.dp

/**
 * Indents [content] by [depth] and draws one vertical guide per ancestor level, producing
 * continuous lines that visually group children with their page / section.
 */
@Composable
fun HierarchyRow(
    depth: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                for (level in 0 until depth) {
                    val x = (GuideOffset + IndentStep * level).toPx()
                    val isPageLevel = level == 0
                    drawLine(
                        color = if (isPageLevel) PageAccent else SectionAccent.copy(alpha = 0.35f),
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = (if (isPageLevel) PageGuideWidth else SectionGuideWidth).toPx(),
                    )
                }
            }
            .padding(start = GuideOffset + IndentStep * depth + 8.dp, end = 16.dp),
    ) {
        content()
    }
}
