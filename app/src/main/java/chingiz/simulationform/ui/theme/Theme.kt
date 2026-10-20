package chingiz.simulationform.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ColorScheme = darkColorScheme(
    primary = PageAccent,
    onPrimary = Color.White,
    secondary = SectionAccent,
    onSecondary = Color.Black,
    secondaryContainer = SectionAccent.copy(alpha = 0.25f),
    onSecondaryContainer = OnSurface,
    background = Background,
    onBackground = OnSurface,
    surface = Background,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    surfaceContainer = Surface,
    surfaceContainerHigh = SurfaceVariant,
    outline = Outline,
    outlineVariant = Outline,
    error = ErrorRed,
)

/** Fixed (non-dynamic) palette so hierarchy colors stay consistent across devices. */
@Composable
fun SimulationFormTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColorScheme,
        typography = Typography,
        content = content,
    )
}
