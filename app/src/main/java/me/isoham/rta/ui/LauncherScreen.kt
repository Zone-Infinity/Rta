package me.isoham.rta.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import me.isoham.rta.adapter.AppAdapter
import me.isoham.rta.data.AppInfo

@Composable
fun AppList(
    onAppClick: (AppInfo) -> Unit
) {
    val context = LocalContext.current

    // --- STATE (single source of truth) ---
    var state by remember { mutableStateOf(LauncherState()) }

    // --- CONTROLLER (stateless, safe) ---
    val controller = remember { LauncherController(context) }

    // --- UI helpers ---
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // --- RecyclerView adapter ---
    val adapter = remember {
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
            onQueryChange = { it ->
                controller.onSearchChange(state, it) { state = it }
                adapter.filter(it)
            },
            onSearch = {
                adapter.getTopApp()?.let { it ->
                    onAppClick(it)
                    controller.closeSearch(state) { state = it }
                    adapter.filter("")
                }
            }
        )

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
                    controller.toggleFavorite(state, app.packageName) { state = it }
                    adapter.updateApps(state.apps)
                    adapter.filter(state.query)
                },
                onHide = {
                    controller.hideApp(state, app.packageName) { state = it }
                    adapter.updateApps(state.apps)
                    adapter.filter(state.query)
                }
            )
        }
    }
}