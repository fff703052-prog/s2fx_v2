package com.erafox.s2fx
import android.content.Context
object Prefs {
    private const val NAME = "s2fx"
    fun get(ctx: Context) = ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE)
    var espEnabled = true
    var triggerEnabled = false
    var triggerDelayMs = 60
    var triggerRadiusPx = 6
    var enemyColorTolerance = 40
    var overlayAlpha = 120
}
