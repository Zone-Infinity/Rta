package me.isoham.rta.data

import android.content.Context
import androidx.core.content.edit
import java.security.MessageDigest

object LauncherPrefs {
    private const val PREFS_NAME = "rta_launcher_prefs"
    private const val KEY_HIDDEN_APPS = "hidden_apps"
    private const val KEY_FAVORITE_APPS = "favorite_apps"
    private const val KEY_PIN_HASH = "pin_hash"
    private const val KEY_HIDE_HINT_SHOWN = "hide_hint_shown"

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

    fun hasPin(context: Context): Boolean {
        return prefs(context).contains(KEY_PIN_HASH)
    }

    fun savePin(context: Context, pin: String) {
        prefs(context)
            .edit {
                putString(KEY_PIN_HASH, sha256(pin))
            }
    }

    fun verifyPin(context: Context, pin: String): Boolean {
        val stored = prefs(context).getString(KEY_PIN_HASH, null) ?: return false
        return stored == sha256(pin)
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun shouldShowHideHint(context: Context): Boolean {
        return !prefs(context).getBoolean(KEY_HIDE_HINT_SHOWN, false)
    }

    fun markHideHintShown(context: Context) {
        prefs(context).edit {
            putBoolean(KEY_HIDE_HINT_SHOWN, true)
        }
    }
}