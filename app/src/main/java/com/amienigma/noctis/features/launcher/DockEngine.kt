package com.amienigma.noctis.features.launcher

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

/**
 * DockEngine for NOCTIS OS
 * Manages the bottom dock / hotseat: app placement, drag-and-drop reordering,
 * folder support, and persistence.
 * 
 * @version 1.0.0
 * @date 2026-11-19
 */
data class DockItem(
    val id: String,
    val packageName: String? = null,
    val label: String,
    val isFolder: Boolean = false,
    val children: List<DockItem> = emptyList()
)

class DockEngine {
    private val _items: SnapshotStateList<DockItem> = mutableStateListOf()
    val items: List<DockItem> get() = _items

    private val maxSlots = 5

    fun addApp(packageName: String, label: String): Boolean {
        if (_items.size >= maxSlots) return false
        _items.add(DockItem(id = packageName, packageName = packageName, label = label))
        return true
    }

    fun removeAt(index: Int) {
        if (index in _items.indices) _items.removeAt(index)
    }

    fun move(from: Int, to: Int) {
        if (from in _items.indices && to in _items.indices) {
            val item = _items.removeAt(from)
            _items.add(to, item)
        }
    }

    fun createFolder(name: String, indices: List<Int>) {
        val children = indices.sortedDescending().mapNotNull { i ->
            if (i in _items.indices) _items.removeAt(i) else null
        }
        if (children.isNotEmpty()) {
            _items.add(DockItem(id = "folder-$name", label = name, isFolder = true, children = children))
        }
    }

    fun expandFolder(index: Int) {
        val folder = _items.getOrNull(index) ?: return
        if (!folder.isFolder) return
        _items.removeAt(index)
        folder.children.forEach { _items.add(it) }
    }

    fun clear() {
        _items.clear()
    }

    fun export(): List<Map<String, Any?>> {
        return _items.map { item ->
            mapOf(
                "id" to item.id,
                "packageName" to item.packageName,
                "label" to item.label,
                "isFolder" to item.isFolder,
                "children" to item.children.map { c -> c.packageName }
            )
        }
    }

    fun import(data: List<Map<String, Any?>>) {
        clear()
        data.forEach { map ->
            val item = DockItem(
                id = map["id"] as String,
                packageName = map["packageName"] as? String,
                label = map["label"] as String,
                isFolder = map["isFolder"] as? Boolean ?: false
            )
            _items.add(item)
        }
    }
}
