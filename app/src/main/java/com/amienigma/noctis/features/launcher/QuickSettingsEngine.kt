package com.amienigma.noctis.features.launcher

/**
 * QuickSettingsEngine for NOCTIS OS
 * Manages quick settings tiles, brightness, volume, network toggles,
 * and custom tile registration for the Galaxy Edition launcher.
 * 
 * @version 1.0.0
 * @date 2026-11-18
 */

class QuickSettingsEngine {
    private val tiles = mutableMapOf<String, Tile>()
    private var brightness = 0.7f
    private var volume = 0.5f
    private var wifiEnabled = true
    private var bluetoothEnabled = false
    private var airplaneMode = false

    data class Tile(
        val id: String,
        val label: String,
        val icon: String,
        var enabled: Boolean = true,
        val onToggle: (Boolean) -> Unit = {}
    )

    init {
        registerDefaultTiles()
    }

    private fun registerDefaultTiles() {
        tiles["wifi"] = Tile("wifi", "Wi-Fi", "wifi", wifiEnabled) { wifiEnabled = it }
        tiles["bluetooth"] = Tile("bluetooth", "Bluetooth", "bluetooth", bluetoothEnabled) { bluetoothEnabled = it }
        tiles["airplane"] = Tile("airplane", "Airplane", "airplane", airplaneMode) { airplaneMode = it }
        tiles["brightness"] = Tile("brightness", "Brightness", "sun", true)
        tiles["volume"] = Tile("volume", "Volume", "volume", true)
        tiles["flashlight"] = Tile("flashlight", "Flashlight", "flashlight", false)
        tiles["rotation"] = Tile("rotation", "Rotation", "rotate", true)
        tiles["dnd"] = Tile("dnd", "Do Not Disturb", "moon", false)
    }

    fun registerTile(tile: Tile) {
        tiles[tile.id] = tile
    }

    fun toggle(id: String): Boolean {
        val tile = tiles[id] ?: return false
        tile.enabled = !tile.enabled
        tile.onToggle(tile.enabled)
        return tile.enabled
    }

    fun setBrightness(value: Float) {
        brightness = value.coerceIn(0f, 1f)
    }

    fun setVolume(value: Float) {
        volume = value.coerceIn(0f, 1f)
    }

    fun getState(): Map<String, Any> = mapOf(
        "brightness" to brightness,
        "volume" to volume,
        "wifi" to wifiEnabled,
        "bluetooth" to bluetoothEnabled,
        "airplane" to airplaneMode,
        "tiles" to tiles.values.map { mapOf("id" to it.id, "label" to it.label, "enabled" to it.enabled) }
    )

    fun getTile(id: String) = tiles[id]
}

// Simple usage helper
fun createQuickSettings(): QuickSettingsEngine = QuickSettingsEngine()
