package me.isoham.rta.ui

import me.isoham.rta.data.AppInfo

data class LauncherState(
    val apps: List<AppInfo> = emptyList(),
    val query: String = "",
    val searchActive: Boolean = false,
    val selectedApp: AppInfo? = null,
    val hiddenApps: MutableSet<String> = mutableSetOf(),
    val favoriteApps: MutableSet<String> = mutableSetOf()
)