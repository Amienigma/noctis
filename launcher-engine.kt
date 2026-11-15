/**
 * LauncherEngine for NOCTIS OS Galaxy Edition
 * Core launcher logic for app grid, folders, and gestures.
 * Daily code addition 2026-11-14.
 */

package com.amienigma.noctis

data class AppInfo(
    val id: String,
    val name: String,
    val packageName: String,
    val icon: String
)

class LauncherEngine {
    private val apps = mutableListOf<AppInfo>()
    private val folders = mutableMapOf<String, List<AppInfo>>()
    var currentPage = 0
    var gridColumns = 4

    fun loadApps(appList: List<AppInfo>) {
        apps.clear()
        apps.addAll(appList)
    }

    fun createFolder(name: String, appIds: List<String>) {
        val selected = apps.filter { it.id in appIds }
        folders[name] = selected
    }

    fun getPageApps(page: Int): List<AppInfo> {
        val start = page * (gridColumns * 6)
        return apps.drop(start).take(gridColumns * 6)
    }

    fun handleGesture(type: String, startX: Float, endX: Float) {
        when (type) {
            "swipe-left" -> if (currentPage < maxPage()) currentPage++
            "swipe-right" -> if (currentPage > 0) currentPage--
            "swipe-up" -> openSearch()
        }
    }

    private fun maxPage(): Int = (apps.size / (gridColumns * 6))

    private fun openSearch() {
        // Launch search overlay
    }

    fun pinApp(appId: String) {
        // Move to dock
    }
}

// Extended utilities
fun sortAppsAlphabetically(apps: List<AppInfo>): List<AppInfo> =
    apps.sortedBy { it.name.lowercase() }
