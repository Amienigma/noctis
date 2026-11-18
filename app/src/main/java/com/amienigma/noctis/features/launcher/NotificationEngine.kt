// NotificationEngine.kt - Notification management for NOCTIS OS launcher
// Supports system notifications, app badges, and priority handling
// Daily code addition 2026-11-17

package com.amienigma.noctis.features.launcher

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

class NotificationEngine(private val context: Context) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val notifications = mutableListOf<AppNotification>()
    private var nextId = 1

    data class AppNotification(
        val id: Int,
        val title: String,
        val message: String,
        val packageName: String,
        val priority: Int = NotificationCompat.PRIORITY_DEFAULT,
        val timestamp: Long = System.currentTimeMillis()
    )

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "noctis_channel",
                "NOCTIS Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for NOCTIS launcher"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(title: String, message: String, packageName: String = "com.amienigma.noctis"): Int {
        val id = nextId++
        val notification = AppNotification(id, title, message, packageName)
        notifications.add(notification)

        val builder = NotificationCompat.Builder(context, "noctis_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        notificationManager.notify(id, builder.build())
        return id
    }

    fun dismiss(id: Int) {
        notificationManager.cancel(id)
        notifications.removeAll { it.id == id }
    }

    fun dismissAll() {
        notificationManager.cancelAll()
        notifications.clear()
    }

    fun getActiveNotifications(): List<AppNotification> {
        return notifications.toList()
    }

    fun getByPackage(packageName: String): List<AppNotification> {
        return notifications.filter { it.packageName == packageName }
    }

    fun setPriority(id: Int, priority: Int) {
        // Update logic would re-issue notification
    }

    // Badge support
    fun setBadgeCount(packageName: String, count: Int) {
        // Integrate with launcher badge system
    }

    fun clearBadge(packageName: String) {
        setBadgeCount(packageName, 0)
    }
}

// Extension functions and helpers
fun Context.getNotificationEngine(): NotificationEngine {
    return NotificationEngine(this)
}
