package me.isoham.rta.ui

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.isoham.rta.model.AppInfo

@Composable
fun AppList(
    apps: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column {
        // Search bar
        TextField(
            value = query,
            onValueChange = { query = it },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .height(if (query.isEmpty()) 1.dp else 56.dp)
                .alpha(if (query.isEmpty()) 0f else 1f),
            placeholder = { Text("Search apps") }
        )


        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                RecyclerView(context).apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = AppAdapter(apps, onAppClick)
                    setHasFixedSize(true)
                }
            },
            update = { recyclerView ->
                (recyclerView.adapter as AppAdapter).filter(query)
            }
        )
    }
}