package com.amienigma.noctis.features.launcher

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

/**
 * FolderEngine for NOCTIS OS Launcher
 * Manages app folders, grouping, naming, and layout on home screens.
 */
class FolderEngine {
    data class Folder(
        val id: String,
        var name: String,
        val apps: MutableList<String> = mutableListOf(),
        var position: Pair<Int, Int> = 0 to 0,
        var color: Long = 0xFF1A1A2E
    )

    private val folders = mutableStateListOf<Folder>()
    var activeFolder by mutableStateOf<Folder?>(null)
        private set

    fun createFolder(name: String, apps: List<String> = emptyList(), position: Pair<Int, Int> = 0 to 0): Folder {
        val folder = Folder(
            id = "folder-${System.currentTimeMillis()}",
            name = name,
            apps = apps.toMutableList(),
            position = position
        )
        folders.add(folder)
        return folder
    }

    fun addAppToFolder(folderId: String, appId: String): Boolean {
        val folder = folders.find { it.id == folderId } ?: return false
        if (!folder.apps.contains(appId)) {
            folder.apps.add(appId)
            return true
        }
        return false
    }

    fun removeAppFromFolder(folderId: String, appId: String): Boolean {
        val folder = folders.find { it.id == folderId } ?: return false
        return folder.apps.remove(appId)
    }

    fun renameFolder(folderId: String, newName: String): Boolean {
        val folder = folders.find { it.id == folderId } ?: return false
        folder.name = newName
        return true
    }

    fun moveFolder(folderId: String, x: Int, y: Int): Boolean {
        val folder = folders.find { it.id == folderId } ?: return false
        folder.position = x to y
        return true
    }

    fun deleteFolder(folderId: String): Boolean {
        return folders.removeAll { it.id == folderId }
    }

    fun openFolder(folderId: String) {
        activeFolder = folders.find { it.id == folderId }
    }

    fun closeFolder() {
        activeFolder = null
    }

    fun getFolders(): List<Folder> = folders.toList()

    fun getFolder(folderId: String): Folder? = folders.find { it.id == folderId }

    fun getAppsInFolder(folderId: String): List<String> =
        folders.find { it.id == folderId }?.apps ?: emptyList()

    // Advanced
    fun mergeFolders(sourceId: String, targetId: String): Boolean {
        val source = folders.find { it.id == sourceId } ?: return false
        val target = folders.find { it.id == targetId } ?: return false
        target.apps.addAll(source.apps.filter { !target.apps.contains(it) })
        folders.remove(source)
        return true
    }

    fun autoNameFolder(folderId: String) {
        val folder = folders.find { it.id == folderId } ?: return
        if (folder.apps.isNotEmpty()) {
            folder.name = "Folder (${folder.apps.size})"
        }
    }

    fun setFolderColor(folderId: String, color: Long) {
        folders.find { it.id == folderId }?.color = color
    }

    fun exportFolders(): List<Map<String, Any>> {
        return folders.map { f ->
            mapOf(
                "id" to f.id,
                "name" to f.name,
                "apps" to f.apps,
                "position" to listOf(f.position.first, f.position.second),
                "color" to f.color
            )
        }
    }

    fun clearAll() {
        folders.clear()
        activeFolder = null
    }

    // End of FolderEngine
}
