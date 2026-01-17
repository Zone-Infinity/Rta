package me.isoham.rta.system

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.net.toUri

object AppIntents {

    fun launch(context: Context, packageName: String) {
        context.packageManager
            .getLaunchIntentForPackage(packageName)
            ?.let(context::startActivity)
    }

    fun uninstall(context: Context, packageName: String) {
        val intent = Intent(Intent.ACTION_UNINSTALL_PACKAGE).apply {
            data = "package:$packageName".toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    fun appInfo(context: Context, packageName: String) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = "package:$packageName".toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}