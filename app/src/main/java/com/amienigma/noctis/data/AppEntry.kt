package com.amienigma.noctis.data

import androidx.compose.ui.graphics.ImageBitmap

data class AppEntry(
    val label: String,
    val packageName: String,
    val activityName: String,
    val icon: ImageBitmap,
) {
    val key: String get() = "$packageName/$activityName"
}
