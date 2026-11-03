package com.amienigma.noctis.features.launcher

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.amienigma.noctis.data.AppEntry
import com.amienigma.noctis.data.AppRepository
import com.amienigma.noctis.data.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LauncherState(
    val apps: List<AppEntry> = emptyList(),
    val dockKeys: List<String> = emptyList(),
    val query: String = "",
    val loading: Boolean = true,
    val error: String? = null,
) {
    val dock: List<AppEntry> get() = dockKeys.mapNotNull { k -> apps.firstOrNull { it.key == k } }
    val filtered: List<AppEntry>
        get() = if (query.isBlank()) apps else apps.filter {
            it.label.contains(query, ignoreCase = true) || it.packageName.contains(query, ignoreCase = true)
        }
}

class LauncherViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = AppRepository(application)
    private val prefs = PreferencesRepository(application)

    private val _state = MutableStateFlow(LauncherState())
    val state: StateFlow<LauncherState> = _state.asStateFlow()

    private val packageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) = refresh()
    }

    init {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_CHANGED)
            addDataScheme("package")
        }
        ContextCompat.registerReceiver(application, packageReceiver, filter, ContextCompat.RECEIVER_EXPORTED)
        viewModelScope.launch { prefs.dock.collect { keys -> _state.update { it.copy(dockKeys = keys) } } }
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            runCatching { repo.loadApps() }
                .onSuccess { apps -> _state.update { it.copy(apps = apps, loading = false, error = null) } }
                .onFailure { e -> _state.update { it.copy(loading = false, error = e.message ?: "Could not load apps") } }
        }
    }

    fun setQuery(query: String) = _state.update { it.copy(query = query) }

    fun launch(app: AppEntry) {
        if (!repo.launch(app)) {
            Toast.makeText(getApplication(), "${app.label} is unavailable", Toast.LENGTH_SHORT).show()
            refresh()
        }
    }

    /** Pin or unpin an app on the dock (max 4). */
    fun toggleDock(app: AppEntry) {
        val current = _state.value.dockKeys
        val next = when {
            app.key in current -> current - app.key
            current.size < MAX_DOCK -> current + app.key
            else -> current
        }
        viewModelScope.launch { prefs.setDock(next) }
    }

    override fun onCleared() {
        getApplication<Application>().unregisterReceiver(packageReceiver)
    }

    private companion object { const val MAX_DOCK = 4 }
}
