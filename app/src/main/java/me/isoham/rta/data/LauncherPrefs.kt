package me.isoham.rta.data

import android.content.Context
import androidx.core.content.edit

object LauncherPrefs {
    private const val PREFS_NAME = "rta_launcher_prefs"
    private const val KEY_HIDDEN_APPS = "hidden_apps"
    private const val KEY_FAVORITE_APPS = "favorite_apps"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun loadHiddenApps(context: Context): MutableSet<String> {
        return prefs(context)
            .getStringSet(KEY_HIDDEN_APPS, emptySet())
            ?.toMutableSet()
            ?: mutableSetOf()
    }

    fun loadFavoriteApps(context: Context): MutableSet<String> {
        return prefs(context)
            .getStringSet(KEY_FAVORITE_APPS, emptySet())
            ?.toMutableSet()
            ?: mutableSetOf()
    }

    fun saveHiddenApps(context: Context, hidden: Set<String>) {
        prefs(context)
            .edit {
                putStringSet(KEY_HIDDEN_APPS, hidden)
            }
    }

    fun saveFavoriteApps(context: Context, favorites: Set<String>) {
        prefs(context)
            .edit {
                putStringSet(KEY_FAVORITE_APPS, favorites)
            }
    }
}