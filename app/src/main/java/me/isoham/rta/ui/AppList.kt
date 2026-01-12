package me.isoham.rta.ui

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var searchActive by remember { mutableStateOf(false) }

    LaunchedEffect(searchActive) {
        if (searchActive) {
            focusRequester.requestFocus()
            keyboardController?.show()
        } else {
            focusManager.clearFocus(force = true)
            keyboardController?.hide()
        }
    }

    BackHandler(enabled = searchActive) {
        // Clear search state
        searchActive = false
        query = ""
    }

    Column {
        // Search bar
        TextField(
            value = query,
            onValueChange = { query = it },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                cursorColor =
                    if (query.isEmpty()) Color.Transparent
                    else MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .height(if (searchActive) 56.dp else 1.dp)
                .alpha(if (searchActive) 1f else 0f),
            placeholder = { Text("Search apps") },
        )


        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                RecyclerView(context).apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = AppAdapter(apps, onAppClick)
                    setHasFixedSize(true)

                    addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                            super.onScrolled(rv, dx, dy)

                            val atTop = !rv.canScrollVertically(-1)

                            // ENTER search: only when pulling DOWN at top (dy < 0)
                            if (atTop && dy <= 0 && !searchActive && rv.scrollState == RecyclerView.SCROLL_STATE_DRAGGING) {
                                searchActive = true
                                return
                            }

                            // EXIT search: only when scrolling DOWN and query is empty
                            if (searchActive && dy > 0 && query.isEmpty()) {
                                searchActive = false
                            }
                        }
                    })
                }
            },
            update = { recyclerView ->
                (recyclerView.adapter as AppAdapter).filter(query)
            }
        )
    }
}