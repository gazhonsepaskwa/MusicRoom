package be.nalebrun.musicroom.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary      = Color.White,
    secondary    = Color.Gray,
    tertiary     = Color.Gray,
    background   = Color.Black,
    onBackground = Color.White,
    surface      = Color.Black,
    onSurface    = Color.White,
)

private val LightColorScheme = lightColorScheme(
    primary      = Color.Black,
    secondary    = Color.Gray,
    tertiary     = Color.Gray,
    background   = Color.White,
    onBackground = Color.Black,
    surface      = Color.White,
    onSurface    = Color.Black,
)

@Composable
fun MusicRoomTheme(
    darkTheme : Boolean = isSystemInDarkTheme(),
    content   : @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else      -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}