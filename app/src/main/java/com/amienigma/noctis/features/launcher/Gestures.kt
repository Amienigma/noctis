package com.amienigma.noctis.features.launcher

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.PointerInputScope

suspend fun PointerInputScope.detectTapGesturesCompat(onTap: () -> Unit) =
    detectTapGestures(onTap = { onTap() })
