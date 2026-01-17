package me.isoham.rta.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import me.isoham.rta.adapter.AppAdapter
import me.isoham.rta.data.AppInfo
import me.isoham.rta.data.LauncherPrefs

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LauncherScreen(
    onAppClick: (AppInfo) -> Unit
) {
    val context = LocalContext.current

    // --- STATE (single source of truth) ---
    var state by remember {
        mutableStateOf(
            LauncherState(
                hiddenApps = LauncherPrefs.loadHiddenApps(context),
                favoriteApps = LauncherPrefs.loadFavoriteApps(context)
            )
        )
    }
    var showHiddenManager by remember { mutableStateOf(false) }
    var pinError by remember { mutableStateOf<String?>(null) }

    // --- CONTROLLER (stateless, safe) ---
    val controller = remember { LauncherController(context) }

    // --- UI helpers ---
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // --- RecyclerView adapter ---
    val adapter = remember(
        state.hiddenApps,
        state.favoriteApps
    ) {
        AppAdapter(
            state.apps,
            state.hiddenApps,
            state.favoriteApps,
            onClick = onAppClick,
            onLongClick = { app ->
                controller.selectApp(state, app) { state = it }
            }
        )
    }

    /* ---------------- INITIAL LOAD ---------------- */

    LaunchedEffect(Unit) {
        controller.loadApps(state) { state = it }
    }

    /* ----------- KEEP ADAPTER IN SYNC ------------- */

    LaunchedEffect(state.apps) {
        adapter.updateApps(state.apps)
        adapter.filter(state.query)
    }

    /* --------------- KEYBOARD -------------------- */

    LaunchedEffect(state.searchActive) {
        if (state.searchActive) {
            focusRequester.requestFocus()
            keyboardController?.show()
        } else {
            focusManager.clearFocus(force = true)
            keyboardController?.hide()
        }
    }

    /* ---------------- BACK ------------------------ */

    BackHandler(enabled = state.searchActive) {
        controller.closeSearch(state) { state = it }
        adapter.filter("")
    }

    /* ---------- APP INSTALL / UNINSTALL ----------- */

    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                controller.loadApps(state) { state = it }
            }
        }

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_CHANGED)
            addDataScheme("package")
        }

        context.registerReceiver(receiver, filter)
        onDispose { context.unregisterReceiver(receiver) }
    }

    /* ---------------- UI -------------------------- */

    Column {
        SearchBar(
            query = state.query,
            active = state.searchActive,
            focusRequester = focusRequester,
            onQueryChange = { input ->
                // Secret trigger
                if (input == "#..") {
                    showHiddenManager = true

                    // Reset search UI
                    controller.closeSearch(state) { state = it }
                    adapter.filter("")
                    return@SearchBar
                }

                controller.onSearchChange(state, input) { state = it }
                adapter.filter(input)
            },
            onSearch = {
                adapter.getTopApp()?.let { it ->
                    onAppClick(it)
                    controller.closeSearch(state) { state = it }
                    adapter.filter("")
                }
            }
        )
        key(state.hiddenApps, state.favoriteApps) {
            AppListView(
                modifier = Modifier.fillMaxSize(),
                adapter = adapter
            ) { dy, atTop ->

                // ENTER search
                if (atTop && dy < 0 && !state.searchActive) {
                    controller.openSearch(state) { state = it }
                }

                // EXIT search
                if (state.searchActive && dy > 8 && state.query.isEmpty()) {
                    controller.closeSearch(state) { state = it }
                    adapter.filter("")
                }
            }
        }

        state.selectedApp?.let { app ->
            AppMenuSheet(
                context = context,
                app = app,
                isFavorite = state.favoriteApps.contains(app.packageName),
                onDismiss = {
                    controller.selectApp(state, null) { state = it }
                },
                onOpen = { onAppClick(app) },
                onToggleFavorite = {
                    controller.toggleFavorite(state, app.packageName) { newState ->
                        state = newState
                        LauncherPrefs.saveFavoriteApps(context, state.favoriteApps)
                    }
                    adapter.updateApps(state.apps)
                    adapter.filter(state.query)
                },
                onHide = {
                    controller.hideApp(state, app.packageName) { newState ->
                        state = newState
                        LauncherPrefs.saveHiddenApps(context, state.hiddenApps)

                        if (LauncherPrefs.shouldShowHideHint(context)) {
                            Toast.makeText(
                                context,
                                "App hidden. Type '#..' in search to manage hidden apps.",
                                Toast.LENGTH_LONG
                            ).show()
                            LauncherPrefs.markHideHintShown(context)
                        }
                    }
                    adapter.updateApps(state.apps)
                    adapter.filter(state.query)
                }
            )
        }
    }

    if (showHiddenManager) {
        ProtectedHiddenApps(
            context = context,
            onClose = {
                showHiddenManager = false

                state = state.copy(
                    hiddenApps = LauncherPrefs.loadHiddenApps(context)
                )
                adapter.updateApps(state.apps)
                adapter.filter(state.query)
            }
        )
    }
}