package me.isoham.rta.ui

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import me.isoham.rta.data.LauncherPrefs

private enum class PinStage {
    SET_PIN,
    VERIFY_PIN,
    ACCESS_GRANTED,
    CHANGE_PIN_VERIFY,
    CHANGE_PIN_SET
}

@Composable
fun ProtectedHiddenApps(
    context: Context,
    onClose: () -> Unit
) {
    var stage by remember {
        mutableStateOf(
            if (LauncherPrefs.hasPin(context))
                PinStage.VERIFY_PIN
            else
                PinStage.SET_PIN
        )
    }

    var error by remember { mutableStateOf<String?>(null) }

    // BACK always closes the protected flow
    BackHandler {
        onClose()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (stage) {

            /* ---------- FIRST TIME SETUP ---------- */
            PinStage.SET_PIN -> {
                PinGate(
                    title = "Set PIN",
                    error = error,
                    onSubmit = { pin ->
                        LauncherPrefs.savePin(context, pin)
                        error = null
                        stage = PinStage.ACCESS_GRANTED
                    }
                )
            }

            /* ---------- NORMAL UNLOCK ---------- */
            PinStage.VERIFY_PIN -> {
                PinGate(
                    title = "Enter PIN",
                    error = error,
                    onSubmit = { pin ->
                        if (LauncherPrefs.verifyPin(context, pin)) {
                            error = null
                            stage = PinStage.ACCESS_GRANTED
                        } else {
                            error = "Wrong PIN"
                        }
                    }
                )
            }

            /* ---------- CHANGE PIN (STEP 1) ---------- */
            PinStage.CHANGE_PIN_VERIFY -> {
                PinGate(
                    title = "Enter current PIN",
                    error = error,
                    onSubmit = { pin ->
                        if (LauncherPrefs.verifyPin(context, pin)) {
                            error = null
                            stage = PinStage.CHANGE_PIN_SET
                        } else {
                            error = "Wrong PIN"
                        }
                    }
                )
            }

            /* ---------- CHANGE PIN (STEP 2) ---------- */
            PinStage.CHANGE_PIN_SET -> {
                PinGate(
                    title = "Set new PIN",
                    error = error,
                    onSubmit = { pin ->
                        LauncherPrefs.savePin(context, pin)
                        error = null
                        stage = PinStage.ACCESS_GRANTED
                    }
                )
            }

            /* ---------- ACCESS GRANTED ---------- */
            PinStage.ACCESS_GRANTED -> {
                HiddenAppsScreen(
                    context = context,
                    onChangePin = { stage = PinStage.CHANGE_PIN_VERIFY },
                    onHiddenAppsChanged = { /* no-op here */ }
                )
            }
        }
    }
}