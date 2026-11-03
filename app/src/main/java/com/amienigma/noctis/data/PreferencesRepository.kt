package com.amienigma.noctis.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.store by preferencesDataStore(name = "noctis_prefs")

/** Persists the pinned dock as an ordered list of "package/activity" keys. */
class PreferencesRepository(private val context: Context) {
    private val dockKey = stringPreferencesKey("dock")

    val dock: Flow<List<String>> = context.store.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e } // corrupted file -> defaults
        .map { prefs -> prefs[dockKey]?.split("|")?.filter { it.isNotBlank() } ?: emptyList() }

    suspend fun setDock(keys: List<String>) {
        context.store.edit { it[dockKey] = keys.joinToString("|") }
    }
}
