package me.isoham.rta.ui

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.isoham.rta.data.AppRepository
import me.isoham.rta.data.LauncherPrefs
import me.isoham.rta.system.PackageWatcher

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HiddenAppsScreen(
    context: Context,
    onChangePin: () -> Unit,
    onHiddenAppsChanged: () -> Unit
) {
    var hiddenApps by remember {
        mutableStateOf<Set<String>>(LauncherPrefs.loadHiddenApps(context))
    }

    var allApps by remember {
        mutableStateOf(AppRepository.loadApps(context))
    }

    DisposableEffect(Unit) {
        val watcher = PackageWatcher(context) {
            allApps = AppRepository.loadApps(context)
            hiddenApps = hiddenApps.intersect(
                allApps.map { it.packageName }.toSet()
            ).toMutableSet()
            LauncherPrefs.saveHiddenApps(context, hiddenApps)
        }

        watcher.register()
        onDispose { watcher.unregister() }
    }

    val hiddenAppInfos by remember(hiddenApps, allApps) {
        derivedStateOf {
            allApps.filter { hiddenApps.contains(it.packageName) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hidden Apps") },
                actions = {
                    Text(
                        "Change PIN",
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable { onChangePin() }
                    )
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            if (hiddenAppInfos.isEmpty()) {
                Text(
                    "No hidden apps",
                    modifier = Modifier.padding(24.dp)
                )
            } else {
                hiddenAppInfos.forEach { app ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(app.name)

                        Text(
                            "Unhide",
                            modifier = Modifier.clickable {
                                hiddenApps = hiddenApps - app.packageName
                                LauncherPrefs.saveHiddenApps(context, hiddenApps)
                                onHiddenAppsChanged()
                            },
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}