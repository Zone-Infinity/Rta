package me.isoham.rta.ui

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.isoham.rta.data.AppInfo
import me.isoham.rta.system.AppIntents

@Composable
fun MenuItem(
    text: String,
    onClick: () -> Unit
) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        style = MaterialTheme.typography.bodyLarge
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppMenuSheet(
    context: Context,
    app: AppInfo,
    isFavorite: Boolean,
    onDismiss: () -> Unit,
    onOpen: () -> Unit,
    onToggleFavorite: () -> Unit,
    onHide: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(app.name, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))

            MenuItem("Open") {
                onOpen()
                onDismiss()
            }

            MenuItem(
                if (isFavorite) "Remove from favorites" else "Add to favorites"
            ) {
                onToggleFavorite()
                onDismiss()
            }

            MenuItem("Hide app") {
                onHide()
                onDismiss()
            }

            MenuItem("Uninstall") {
                AppIntents.uninstall(context, app.packageName)
                onDismiss()
            }

            MenuItem("App info") {
                AppIntents.appInfo(context, app.packageName)
                onDismiss()
            }
        }
    }
}