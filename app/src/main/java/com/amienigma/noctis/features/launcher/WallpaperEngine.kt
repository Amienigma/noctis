package com.amienigma.noctis.features.launcher

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * WallpaperEngine for NOCTIS OS
 * Dynamic wallpaper generation, parallax, and theme-reactive backgrounds
 * for the Galaxy Edition launcher.
 */
class WallpaperEngine(private val context: Context) {
    private val _currentWallpaper = MutableStateFlow<Bitmap?>(null)
    val currentWallpaper: StateFlow<Bitmap?> = _currentWallpaper

    private var parallaxOffsetX = 0f
    private var parallaxOffsetY = 0f
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    fun generateSolid(color: Int = Color.BLACK): Bitmap {
        val bmp = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawColor(color)
        _currentWallpaper.value = bmp
        return bmp
    }

    fun generateGradient(startColor: Int, endColor: Int): Bitmap {
        val bmp = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        // Simple vertical gradient approximation
        for (y in 0 until 1920) {
            val ratio = y / 1920f
            val r = (Color.red(startColor) * (1 - ratio) + Color.red(endColor) * ratio).toInt()
            val g = (Color.green(startColor) * (1 - ratio) + Color.green(endColor) * ratio).toInt()
            val b = (Color.blue(startColor) * (1 - ratio) + Color.blue(endColor) * ratio).toInt()
            paint.color = Color.rgb(r, g, b)
            canvas.drawLine(0f, y.toFloat(), 1080f, y.toFloat(), paint)
        }
        _currentWallpaper.value = bmp
        return bmp
    }

    fun applyParallax(offsetX: Float, offsetY: Float) {
        parallaxOffsetX = offsetX.coerceIn(-50f, 50f)
        parallaxOffsetY = offsetY.coerceIn(-50f, 50f)
    }

    fun getParallaxMatrix(): Pair<Float, Float> {
        return Pair(parallaxOffsetX, parallaxOffsetY)
    }

    fun generateNoise(seed: Int = 42): Bitmap {
        val bmp = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
        val random = java.util.Random(seed.toLong())
        for (x in 0 until 1080 step 4) {
            for (y in 0 until 1920 step 4) {
                val gray = random.nextInt(40) + 10
                paint.color = Color.rgb(gray, gray, gray)
                bmp.setPixel(x, y, paint.color)
            }
        }
        _currentWallpaper.value = bmp
        return bmp
    }

    fun setFromTheme(isDark: Boolean) {
        if (isDark) {
            generateGradient(Color.parseColor("#0a0a12"), Color.parseColor("#1a1a2e"))
        } else {
            generateGradient(Color.parseColor("#f5f5f7"), Color.parseColor("#e8e8ed"))
        }
    }

    fun clear() {
        _currentWallpaper.value?.recycle()
        _currentWallpaper.value = null
    }

    fun getInfo(): Map<String, Any> {
        return mapOf(
            "hasWallpaper" to (_currentWallpaper.value != null),
            "parallaxX" to parallaxOffsetX,
            "parallaxY" to parallaxOffsetY
        )
    }
}
