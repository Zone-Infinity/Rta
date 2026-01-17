package me.isoham.rta.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.isoham.rta.adapter.AppAdapter

@Composable
fun AppListView(
    modifier: Modifier = Modifier,
    adapter: AppAdapter,
    onScroll: (dy: Int, atTop: Boolean) -> Unit
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            RecyclerView(context).apply {
                layoutManager = LinearLayoutManager(context)
                this.adapter = adapter
                setHasFixedSize(true)

                // Search is activated when the user scrolls upward
                // while already at the top of the list.
                // This is scroll-based, not gesture-intent-based.
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                        val atTop = !rv.canScrollVertically(-1)
                        onScroll(dy, atTop)
                    }
                })
            }
        }
    )
}