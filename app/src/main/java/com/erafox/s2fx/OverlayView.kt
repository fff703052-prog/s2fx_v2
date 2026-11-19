package com.erafox.s2fx
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import kotlin.concurrent.thread
class OverlayView(ctx: Context) : View(ctx) {
    private val paint = Paint().apply { style = Paint.Style.STROKE; strokeWidth = 3f; color = Color.RED }
    @Volatile private var running = true
    @Volatile private var boxesCache: List<EspEngine.Box> = emptyList()
    init {
        thread(isDaemon = true) {
            while (running) {
                ScreenCaptureService.latest?.let { bmp ->
                    boxesCache = EspEngine.detect(bmp, 40)
                    TriggerEngine.tick(bmp, false, 60, 6)
                    postInvalidate()
                }
                Thread.sleep(30)
            }
        }
    }
    override fun onDraw(canvas: Canvas) { super.onDraw(canvas); EspEngine.draw(canvas, boxesCache, paint) }
    override fun onDetachedFromWindow() { running = false; super.onDetachedFromWindow() }
}
