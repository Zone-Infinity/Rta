package me.isoham.rta.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.isoham.rta.model.AppInfo

@Composable
fun AppList(
    apps: List<AppInfo>, onAppClick: (AppInfo) -> Unit
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(items = apps) { app ->
            AppRow(
                app = app,
                onClick = { onAppClick(app) }
            )
        }
    }
}

@Composable
fun AppRow(
    app: AppInfo, onClick: () -> Unit
) {
    BasicText(
        text = app.name,
        style = TextStyle(
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 16.sp
        ),
        modifier = Modifier
            .clearAndSetSemantics {}
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        maxLines = 1
    )
}