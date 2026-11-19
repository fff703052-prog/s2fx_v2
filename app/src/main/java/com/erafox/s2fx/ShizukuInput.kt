package com.erafox.s2fx
import android.content.Context
object ShizukuInput {
    fun ensureUserService(ctx: Context) {}
    fun tap(x: Int, y: Int) {
        try { Runtime.getRuntime().exec(arrayOf("sh", "-c", "input tap \$x \$y")).waitFor() } catch (_: Throwable) {}
    }
}
