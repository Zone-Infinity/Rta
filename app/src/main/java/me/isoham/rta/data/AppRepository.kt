package me.isoham.rta.data

import android.content.Context
import android.content.Intent

/**
 * Loads launchable apps only.
 *
 * NOTE:
 * Some apps (e.g. Amazon Pay, some banking apps) do NOT declare
 * CATEGORY_LAUNCHER activities and will NOT appear here.
 *
 * They are intentionally excluded in v1.
 * Niagara / Moto use additional heuristics.
 */
object AppRepository {

    fun loadApps(context: Context): List<AppInfo> {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)

        return pm.queryIntentActivities(intent, 0)
            .map {
                AppInfo(
                    name = it.loadLabel(pm).toString(),
                    packageName = it.activityInfo.packageName
                )
            }
            .distinctBy { it.packageName }
            .sortedBy { it.name.lowercase() }
    }
}