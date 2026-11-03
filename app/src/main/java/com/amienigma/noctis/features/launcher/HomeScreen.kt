package com.amienigma.noctis.features.launcher

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amienigma.noctis.core.theme.NoctisColors
import com.amienigma.noctis.data.AppEntry
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
private fun rememberNow(): State<LocalDateTime> = produceState(LocalDateTime.now()) {
    while (true) {
        value = LocalDateTime.now()
        delay(60_000L - System.currentTimeMillis() % 60_000L) // tick on the minute, not every second
    }
}

@Composable
fun HomeScreen(
    dock: List<AppEntry>,
    onOpenDrawer: () -> Unit,
    onLaunch: (AppEntry) -> Unit,
    onUnpin: (AppEntry) -> Unit,
) {
    val context = LocalContext.current
    val now by rememberNow()
    val timeFmt = remember(context) {
        DateTimeFormatter.ofPattern(if (DateFormat.is24HourFormat(context)) "HH:mm" else "h:mm")
    }
    val dateFmt = remember { DateTimeFormatter.ofPattern("EEEE, d MMMM") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NoctisColors.Background.copy(alpha = 0.8f)) // wallpaper shows through
            .windowInsetsPadding(WindowInsets.systemBars)
            .pointerInput(Unit) {
                var total = 0f
                detectVerticalDragGestures(
                    onDragStart = { total = 0f },
                    onVerticalDrag = { _, delta -> total += delta },
                    onDragEnd = { if (total < -120f) onOpenDrawer() },
                )
            }
            .padding(horizontal = 24.dp),
    ) {
        Spacer(Modifier.height(48.dp))
        Text(
            text = now.format(timeFmt),
            fontSize = 88.sp,
            fontWeight = FontWeight.ExtraLight,
            color = NoctisColors.TextPrimary,
        )
        Text(
            text = now.format(dateFmt),
            style = MaterialTheme.typography.bodyLarge,
            color = NoctisColors.TextSecondary,
        )

        Spacer(Modifier.weight(1f))

        // Dock
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(30.dp))
                .background(NoctisColors.Elevated)
                .padding(12.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (dock.isEmpty()) {
                Text(
                    "Open the drawer and long-press an app to pin it here",
                    style = MaterialTheme.typography.bodySmall,
                    color = NoctisColors.TextMuted,
                    modifier = Modifier.padding(16.dp),
                )
            } else {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    dock.forEach { app ->
                        AppIcon(app, onClick = { onLaunch(app) }, onLongClick = { onUnpin(app) })
                    }
                }
            }
        }

        // Drawer handle: also an accessible button for users who can't swipe.
        Box(
            Modifier
                .fillMaxWidth()
                .height(48.dp)
                .semantics { contentDescription = "Open app drawer" }
                .pointerInput(Unit) { detectTapGesturesCompat(onOpenDrawer) },
            contentAlignment = Alignment.Center,
        ) {
            Box(Modifier.size(width = 40.dp, height = 4.dp).clip(RoundedCornerShape(2.dp)).background(NoctisColors.TextMuted))
        }
    }
}
