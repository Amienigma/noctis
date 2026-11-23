/**
 * SearchEngine for NOCTIS OS
 * Handles app search, fuzzy matching, recent searches, and suggestion ranking.
 * Added 2026-11-22 as daily code addition.
 */

package com.amienigma.noctis.features.launcher

import com.amienigma.noctis.data.AppEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SearchEngine {
    private val recentSearches = mutableListOf<String>()
    private val searchHistory = mutableMapOf<String, Int>()
    private val _results = MutableStateFlow<List<AppEntry>>(emptyList())
    val results: StateFlow<List<AppEntry>> = _results
    private val maxRecent = 20
    private var allApps: List<AppEntry> = emptyList()

    fun setApps(apps: List<AppEntry>) {
        allApps = apps
    }

    fun search(query: String): List<AppEntry> {
        if (query.isBlank()) {
            _results.value = emptyList()
            return emptyList()
        }
        val q = query.lowercase().trim()
        val matches = allApps.filter { app ->
            app.label.lowercase().contains(q) ||
            app.packageName.lowercase().contains(q)
        }.sortedByDescending { app ->
            calculateScore(app, q)
        }
        recordSearch(query)
        _results.value = matches
        return matches
    }

    private fun calculateScore(app: AppEntry, query: String): Int {
        var score = 0
        val label = app.label.lowercase()
        if (label.startsWith(query)) score += 100
        if (label.contains(query)) score += 50
        score += (searchHistory[app.packageName] ?: 0) * 10
        if (label.length <= query.length + 3) score += 20
        return score
    }

    private fun recordSearch(query: String) {
        if (query.length < 2) return
        recentSearches.remove(query)
        recentSearches.add(0, query)
        if (recentSearches.size > maxRecent) {
            recentSearches.removeAt(recentSearches.size - 1)
        }
    }

    fun getRecentSearches(): List<String> = recentSearches.toList()

    fun clearRecent() {
        recentSearches.clear()
    }

    fun getSuggestions(query: String, limit: Int = 5): List<String> {
        if (query.isBlank()) return recentSearches.take(limit)
        return recentSearches.filter { it.startsWith(query, ignoreCase = true) }.take(limit)
    }

    fun fuzzyMatch(query: String, threshold: Int = 2): List<AppEntry> {
        return allApps.filter { app ->
            levenshteinDistance(query.lowercase(), app.label.lowercase()) <= threshold
        }
    }

    private fun levenshteinDistance(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }
        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j
        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + cost
                )
            }
        }
        return dp[s1.length][s2.length]
    }

    fun boostApp(packageName: String) {
        searchHistory[packageName] = (searchHistory[packageName] ?: 0) + 1
    }

    fun getTopApps(limit: Int = 10): List<AppEntry> {
        return allApps.sortedByDescending { searchHistory[it.packageName] ?: 0 }.take(limit)
    }

    fun filterByCategory(category: String): List<AppEntry> {
        return allApps.filter { it.category?.equals(category, ignoreCase = true) == true }
    }

    fun getStats(): Map<String, Any> {
        return mapOf(
            "totalApps" to allApps.size,
            "recentSearches" to recentSearches.size,
            "historyEntries" to searchHistory.size,
            "currentResults" to _results.value.size
        )
    }

    fun reset() {
        recentSearches.clear()
        searchHistory.clear()
        _results.value = emptyList()
    }

    companion object {
        const val VERSION = "1.0.0"
        const val DESCRIPTION = "App search and suggestion engine for NOCTIS launcher"
    }
}
