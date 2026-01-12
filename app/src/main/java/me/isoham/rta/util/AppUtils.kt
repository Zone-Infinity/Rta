package me.isoham.rta.util

import android.content.Context
import android.content.Intent
import me.isoham.rta.model.AppInfo

// NOTE:
// Some apps (e.g., Amazon Pay, some banking apps) do NOT declare
// CATEGORY_LAUNCHER activities.
// They will NOT appear here by design.
// Niagara / Moto handle this with extra heuristics.
// Intentionally skipped for v1.
fun getInstalledApps(context: Context): List<AppInfo> {
    val pm = context.packageManager
    val intent = Intent(Intent.ACTION_MAIN, null).apply {
        addCategory(Intent.CATEGORY_LAUNCHER)
    }

    return pm.queryIntentActivities(intent, 0).map {
        AppInfo(
            name = it.loadLabel(pm).toString(),
            packageName = it.activityInfo.packageName
        )
    }
        .distinctBy { it.packageName }
        .sortedBy { it.name.lowercase() }
}

fun launchApp(context: Context, packageName: String) {
    val intent = context.packageManager.getLaunchIntentForPackage(packageName)
    intent?.let { context.startActivity(it) }
}
