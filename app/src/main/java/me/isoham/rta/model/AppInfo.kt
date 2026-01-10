package me.isoham.rta.model

import androidx.compose.runtime.Immutable

@Immutable
data class AppInfo(
    val name: String,
    val packageName: String
)