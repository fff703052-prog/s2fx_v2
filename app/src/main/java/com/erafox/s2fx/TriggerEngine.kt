package com.erafox.s2fx
import android.graphics.Bitmap
import android.os.SystemClock
object TriggerEngine {
    private var lastShot = 0L
    fun tick(bitmap: Bitmap?, enabled: Boolean, delayMs: Int, radiusPx: Int) {
        if (!enabled || bitmap == null) return
        if (SystemClock.uptimeMillis() - lastShot < delayMs) return
        val cx = bitmap.width / 2; val cy = bitmap.height / 2
        ShizukuInput.tap(cx, cy); lastShot = SystemClock.uptimeMillis()
    }
}
