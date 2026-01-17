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
import me.isoham.rta.ui.AppList
import me.isoham.rta.ui.theme.RtaTheme

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
                        AppList(
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