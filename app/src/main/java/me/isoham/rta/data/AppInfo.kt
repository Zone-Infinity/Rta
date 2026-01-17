package me.isoham.rta.data

import androidx.compose.runtime.Immutable

@Immutable
data class AppInfo(
    val name: String,
    val packageName: String
)