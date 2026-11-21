package com.amienigma.noctis.features.launcher

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State

/**
 * DrawerEngine for NOCTIS OS
 * Manages the app drawer: filtering, sorting, grid layout, and search.
 *
 * @version 1.0.0
 * @date 2026-11-20
 */
data class AppInfo(
    val packageName: String,
    val label: String,
    val icon: Any? = null,
    val category: String = "other"
)

class DrawerEngine {
    private val _apps = mutableStateOf<List<AppInfo>>(emptyList())
    val apps: State<List<AppInfo>> = _apps

    private val _filtered = mutableStateOf<List<AppInfo>>(emptyList())
    val filtered: State<List<AppInfo>> = _filtered

    private val _query = mutableStateOf("")
    val query: State<String> = _query

    private val _sortMode = mutableStateOf("alpha")
    val sortMode: State<String> = _sortMode

    fun loadApps(appList: List<AppInfo>) {
        _apps.value = appList
        applyFilterAndSort()
    }

    fun setQuery(q: String) {
        _query.value = q
        applyFilterAndSort()
    }

    fun setSortMode(mode: String) {
        _sortMode.value = mode
        applyFilterAndSort()
    }

    private fun applyFilterAndSort() {
        var list = _apps.value
        val q = _query.value.lowercase()
        if (q.isNotEmpty()) {
            list = list.filter { it.label.lowercase().contains(q) || it.packageName.contains(q) }
        }
        list = when (_sortMode.value) {
            "alpha" -> list.sortedBy { it.label }
            "category" -> list.sortedBy { it.category }
            "recent" -> list // stub
            else -> list
        }
        _filtered.value = list
    }

    fun getGridColumns(screenWidth: Int): Int {
        return when {
            screenWidth > 1200 -> 6
            screenWidth > 800 -> 5
            else -> 4
        }
    }

    fun clearSearch() {
        _query.value = ""
        applyFilterAndSort()
    }

    fun getStats(): Map<String, Any> {
        return mapOf(
            "totalApps" to _apps.value.size,
            "filtered" to _filtered.value.size,
            "query" to _query.value,
            "sort" to _sortMode.value
        )
    }
}
