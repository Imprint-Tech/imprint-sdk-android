package co.imprint.sdk.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt

private val LightColorScheme = lightColorScheme(
  surface = Color("#FAFAFA".toColorInt()),
  onSurface = Color("#232323".toColorInt()),
)

private val DarkColorScheme = darkColorScheme(
  surface = Color("#333333".toColorInt()),
  onSurface = Color("#FAFAFA".toColorInt()),
)

@Composable
internal fun ImprintTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
    content = content,
  )
}
