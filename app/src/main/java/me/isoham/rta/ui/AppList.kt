package me.isoham.rta.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.isoham.rta.model.AppInfo
import me.isoham.rta.util.getInstalledApps

@Composable
fun AppList(
    onAppClick: (AppInfo) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val context = LocalContext.current
    var apps by remember {
        mutableStateOf(getInstalledApps(context))
    }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var searchActive by remember { mutableStateOf(false) }

    val adapter = remember {
        AppAdapter(apps, onAppClick)
    }

    LaunchedEffect(searchActive) {
        if (searchActive) {
            focusRequester.requestFocus()
            keyboardController?.show()
        } else {
            focusManager.clearFocus(force = true)
            keyboardController?.hide()
        }
    }

    LaunchedEffect(apps) {
        adapter.updateApps(apps)
    }

    BackHandler(enabled = searchActive) {
        // Clear search state
        searchActive = false
        query = ""
    }

    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                apps = getInstalledApps(context)
            }
        }

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_CHANGED)
            addDataScheme("package")
        }

        context.registerReceiver(receiver, filter)

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    Column {
        // Search bar
        TextField(
            value = query,
            onValueChange = { query = it },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    adapter.getTopApp()?.let {
                        onAppClick(it)
                        searchActive = false
                        query = ""
                    }
                }
            ),
            colors = TextFieldDefaults.colors(
                cursorColor =
                    if (query.isEmpty()) Color.Transparent
                    else MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .height(if (searchActive) 56.dp else 0.dp)
                .alpha(if (searchActive) 1f else 0f),
            placeholder = { Text("Search apps") },
        )

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                RecyclerView(context).apply {
                    layoutManager = LinearLayoutManager(context)
                    this.adapter = adapter
                    setHasFixedSize(true)

                    addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                            super.onScrolled(rv, dx, dy)

                            val atTop = !rv.canScrollVertically(-1)

                            // ENTER search: only when pulling DOWN at top (dy < 0)
                            if (atTop && dy <= 0 && !searchActive) {
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
            update = { _ ->
                adapter.filter(query)
            }
        )
    }
}