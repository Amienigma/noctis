package com.amienigma.noctis

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.amienigma.noctis.core.theme.NoctisTheme
import com.amienigma.noctis.features.appdrawer.DrawerScreen
import com.amienigma.noctis.features.launcher.HomeScreen
import com.amienigma.noctis.features.launcher.LauncherViewModel

class MainActivity : ComponentActivity() {
    private var homePressed by mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { NoctisTheme { LauncherRoot(homePressed) } }
    }

    // Pressing Home while already on Home closes the drawer.
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.hasCategory(Intent.CATEGORY_HOME)) homePressed++
    }
}

@Composable
private fun LauncherRoot(homePressCount: Int, vm: LauncherViewModel = viewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    var drawerOpen by rememberSaveable { mutableStateOf(false) }

    fun closeDrawer() { drawerOpen = false; vm.setQuery("") }

    LaunchedEffect(homePressCount) { if (homePressCount > 0) closeDrawer() }
    BackHandler(enabled = drawerOpen) { closeDrawer() }

    Box(Modifier.fillMaxSize()) {
        HomeScreen(
            dock = state.dock,
            onOpenDrawer = { drawerOpen = true },
            onLaunch = vm::launch,
            onUnpin = vm::toggleDock,
        )
        AnimatedVisibility(
            visible = drawerOpen,
            enter = slideInVertically { it / 3 } + fadeIn(),
            exit = slideOutVertically { it / 3 } + fadeOut(),
        ) {
            DrawerScreen(
                state = state,
                onQuery = vm::setQuery,
                onLaunch = { app -> closeDrawer(); vm.launch(app) },
                onTogglePin = vm::toggleDock,
                onRetry = vm::refresh,
            )
        }
    }
}
