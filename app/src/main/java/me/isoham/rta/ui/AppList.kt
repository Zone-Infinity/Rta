package me.isoham.rta.ui

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.isoham.rta.model.AppInfo

@Composable
fun AppList(
    apps: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit
) {
    AndroidView(
        modifier = Modifier,
        factory = { context ->
            RecyclerView(context).apply {
                layoutManager = LinearLayoutManager(context)
                adapter = AppAdapter(apps, onAppClick)
                setHasFixedSize(true)
                layoutParams = RecyclerView.LayoutParams(
                    MATCH_PARENT,
                    MATCH_PARENT
                )
            }
        }
    )
}