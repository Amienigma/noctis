package com.amienigma.noctis.features.appdrawer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.amienigma.noctis.core.theme.NoctisColors
import com.amienigma.noctis.data.AppEntry
import com.amienigma.noctis.features.launcher.AppIcon
import com.amienigma.noctis.features.launcher.LauncherState

@Composable
fun DrawerScreen(
    state: LauncherState,
    onQuery: (String) -> Unit,
    onLaunch: (AppEntry) -> Unit,
    onTogglePin: (AppEntry) -> Unit,
    onRetry: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(NoctisColors.Background.copy(alpha = 0.96f))
            .windowInsetsPadding(WindowInsets.systemBars)
            .imePadding()
            .padding(horizontal = 16.dp),
    ) {
        OutlinedTextField(
            value = state.query,
            onValueChange = onQuery,
            singleLine = true,
            placeholder = { Text("Search apps") },
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp),
        )
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when {
                state.loading -> CircularProgressIndicator()
                state.error != null -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Couldn't load apps", color = NoctisColors.Error)
                    TextButton(onClick = onRetry) { Text("Retry") }
                }
                state.filtered.isEmpty() -> Text(
                    "No apps match \"${state.query}\"",
                    color = NoctisColors.TextMuted,
                )
                else -> LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 80.dp), // adapts to phone, fold, tablet
                    contentPadding = PaddingValues(bottom = 24.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(state.filtered, key = { it.key }) { app ->
                        AppIcon(app, onClick = { onLaunch(app) }, onLongClick = { onTogglePin(app) })
                    }
                }
            }
        }
    }
}
