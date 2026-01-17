package me.isoham.rta.ui

import android.content.Context
import me.isoham.rta.data.AppInfo
import me.isoham.rta.data.AppRepository
import me.isoham.rta.data.LauncherPrefs

class LauncherController(
    private val context: Context
) {

    fun loadApps(
        state: LauncherState,
        update: (LauncherState) -> Unit
    ) {
        update(
            state.copy(
                apps = AppRepository.loadApps(context),
                hiddenApps = LauncherPrefs.loadHiddenApps(context),
                favoriteApps = LauncherPrefs.loadFavoriteApps(context)
            )
        )
    }

    fun openSearch(
        state: LauncherState,
        update: (LauncherState) -> Unit
    ) {
        update(state.copy(searchActive = true))
    }

    fun closeSearch(
        state: LauncherState,
        update: (LauncherState) -> Unit
    ) {
        update(state.copy(searchActive = false, query = ""))
    }

    fun onSearchChange(
        state: LauncherState,
        query: String,
        update: (LauncherState) -> Unit
    ) {
        update(state.copy(query = query))
    }

    fun selectApp(
        state: LauncherState,
        app: AppInfo?,
        update: (LauncherState) -> Unit
    ) {
        update(state.copy(selectedApp = app))
    }

    fun toggleFavorite(
        state: LauncherState,
        packageName: String,
        update: (LauncherState) -> Unit
    ) {
        val favorites = state.favoriteApps
        if (!favorites.add(packageName)) favorites.remove(packageName)
        update(state.copy())
    }

    fun hideApp(
        state: LauncherState,
        packageName: String,
        update: (LauncherState) -> Unit
    ) {
        state.hiddenApps.add(packageName)
        update(state.copy())
    }
}