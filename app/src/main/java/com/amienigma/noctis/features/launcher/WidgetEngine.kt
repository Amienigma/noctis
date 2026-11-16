package com.amienigma.noctis.features.launcher

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * WidgetEngine - Manages widgets, shortcuts, and dynamic home screen elements for NOCTIS launcher
 */
data class Widget(
    val id: String,
    val type: WidgetType,
    val title: String,
    val packageName: String? = null,
    val position: Pair<Int, Int> = Pair(0, 0),
    val size: Pair<Int, Int> = Pair(1, 1),
    val config: Map<String, Any> = emptyMap(),
    val enabled: Boolean = true
)

enum class WidgetType {
    CLOCK, WEATHER, BATTERY, SHORTCUT, APP_DRAWER, SEARCH, CUSTOM
}

class WidgetEngine {
    private val _widgets = MutableStateFlow<List<Widget>>(emptyList())
    val widgets: StateFlow<List<Widget>> = _widgets

    private val activeWidgets: SnapshotStateList<Widget> = mutableStateListOf()

    init {
        loadDefaultWidgets()
    }

    fun addWidget(widget: Widget) {
        activeWidgets.add(widget)
        _widgets.value = activeWidgets.toList()
    }

    fun removeWidget(id: String) {
        activeWidgets.removeIf { it.id == id }
        _widgets.value = activeWidgets.toList()
    }

    fun updateWidget(id: String, updater: (Widget) -> Widget) {
        val index = activeWidgets.indexOfFirst { it.id == id }
        if (index >= 0) {
            activeWidgets[index] = updater(activeWidgets[index])
            _widgets.value = activeWidgets.toList()
        }
    }

    fun moveWidget(id: String, newPosition: Pair<Int, Int>) {
        updateWidget(id) { it.copy(position = newPosition) }
    }

    fun resizeWidget(id: String, newSize: Pair<Int, Int>) {
        updateWidget(id) { it.copy(size = newSize) }
    }

    fun getWidgetsForPage(page: Int): List<Widget> {
        return activeWidgets.filter { it.position.first == page && it.enabled }
    }

    private fun loadDefaultWidgets() {
        addWidget(Widget("clock", WidgetType.CLOCK, "Clock", position = Pair(0, 0), size = Pair(2, 1)))
        addWidget(Widget("weather", WidgetType.WEATHER, "Weather", position = Pair(0, 1), size = Pair(2, 1)))
        addWidget(Widget("search", WidgetType.SEARCH, "Search", position = Pair(1, 0), size = Pair(4, 1)))
        addWidget(Widget("drawer", WidgetType.APP_DRAWER, "App Drawer", position = Pair(2, 0), size = Pair(1, 1)))
    }

    fun exportLayout(): List<Widget> = activeWidgets.toList()

    fun importLayout(widgets: List<Widget>) {
        activeWidgets.clear()
        activeWidgets.addAll(widgets)
        _widgets.value = activeWidgets.toList()
    }

    fun getAvailableTypes(): List<WidgetType> = WidgetType.values().toList()

    fun createShortcut(packageName: String, title: String): Widget {
        val widget = Widget(
            id = "shortcut_${System.currentTimeMillis()}",
            type = WidgetType.SHORTCUT,
            title = title,
            packageName = packageName,
            position = Pair(0, activeWidgets.size)
        )
        addWidget(widget)
        return widget
    }

    fun toggleEnabled(id: String) {
        updateWidget(id) { it.copy(enabled = !it.enabled) }
    }

    fun getStats(): Map<String, Any> {
        return mapOf(
            "total" to activeWidgets.size,
            "enabled" to activeWidgets.count { it.enabled },
            "byType" to activeWidgets.groupBy { it.type }.mapValues { it.value.size }
        )
    }
}
