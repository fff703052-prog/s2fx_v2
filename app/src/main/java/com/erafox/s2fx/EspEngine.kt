package com.erafox.s2fx
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
object EspEngine {
    data class Box(val x: Float, val y: Float, val w: Float, val h: Float)
    fun detect(bitmap: Bitmap, tolerance: Int = 40): List<Box> {
        val w = bitmap.width; val h = bitmap.height
        val px = IntArray(w * h); bitmap.getPixels(px, 0, w, 0, 0, w, h)
        var minX = Int.MAX_VALUE; var minY = Int.MAX_VALUE; var maxX = -1; var maxY = -1; var count = 0
        for (y in 0 until h step 4) { for (x in 0 until w step 4) {
            val c = px[y * w + x]; val r = (c shr 16) and 0xFF; val g = (c shr 8) and 0xFF; val b = c and 0xFF
            if (Math.abs(r - 220) <= tolerance && Math.abs(g - 60) <= tolerance && Math.abs(b - 60) <= tolerance) {
                count++; if (x < minX) minX = x; if (y < minY) minY = y; if (x > maxX) maxX = x; if (y > maxY) maxY = y
            }
        } }
        if (count < 40) return emptyList()
        return listOf(Box(minX.toFloat(), minY.toFloat(), (maxX - minX).toFloat(), (maxY - minY).toFloat()))
    }
    fun draw(canvas: Canvas, boxes: List<Box>, paint: Paint) { for (b in boxes) canvas.drawRect(b.x, b.y, b.x + b.w, b.y + b.h, paint) }
}
