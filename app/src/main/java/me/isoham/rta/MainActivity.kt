package me.isoham.rta


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import me.isoham.rta.system.AppIntents
import me.isoham.rta.ui.LauncherScreen
import me.isoham.rta.ui.theme.RtaTheme

/*
RTA v1 scope lock:
- No biometrics
- No calculator gate
- No settings screen
- No animations
- No feeds
- No cloud
- No analytics

This version is intentionally minimal and stable.
Future changes must justify breaking this simplicity.
*/
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Do nothing — HOME should not go back
                }
            }
        )

        setContent {
            RtaTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CompositionLocalProvider(
                        LocalOverscrollConfiguration provides null,
                        LocalLayoutDirection provides LayoutDirection.Ltr,
                        LocalDensity provides LocalDensity.current
                    ) {
                        LauncherScreen(
                            onAppClick = { app ->
                                AppIntents.launch(this, app.packageName)
                            }
                        )
                    }
                }
            }
        }
    }
}