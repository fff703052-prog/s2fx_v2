package com.erafox.s2fx

import android.app.*
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder

class ScreenCaptureService : Service() {

    companion object {
        const val ACTION_START = "start"
        const val EXTRA_RESULT_CODE = "rc"
        const val EXTRA_DATA = "data"
        @Volatile var latest: Bitmap? = null
    }

    private var projection: MediaProjection? = null
    private var vd: VirtualDisplay? = null
    private var reader: ImageReader? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_START) {
            val ch = NotificationChannel("s2fx", "S2FX", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(ch)
            
            val notification = Notification.Builder(this, "s2fx")
                .setContentTitle("S2FX Capture")
                .setSmallIcon(android.R.drawable.ic_menu_camera)
                .setOngoing(true)
                .build()

            if (Build.VERSION.SDK_INT >= 29) {
                startForeground(42, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION)
            } else {
                startForeground(42, notification)
            }

            val rc = intent.getIntExtra(EXTRA_RESULT_CODE, 0)
            @Suppress("DEPRECATION") 
            val data = intent.getParcelableExtra<Intent>(EXTRA_DATA) ?: return START_NOT_STICKY
            
            val mpm = getSystemService(MediaProjectionManager::class.java)
            projection = mpm.getMediaProjection(rc, data)
            val dm = resources.displayMetrics
            
            reader = ImageReader.newInstance(dm.widthPixels, dm.heightPixels, PixelFormat.RGBA_8888, 2)
            reader!!.setOnImageAvailableListener({ r ->
                val img = r.acquireLatestImage() ?: return@setOnImageAvailableListener
                try {
                    val plane = img.planes[0]
                    val bmp = Bitmap.createBitmap(dm.widthPixels, dm.heightPixels, Bitmap.Config.ARGB_8888)
                    bmp.copyPixelsFromBuffer(plane.buffer)
                    latest = bmp
                } catch (_: Throwable) {} finally { img.close() }
            }, null)

            vd = projection!!.createVirtualDisplay(
                "s2fx", dm.widthPixels, dm.heightPixels, dm.densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, reader!!.surface, null, null
            )
        }
        return START_STICKY
    }

    override fun onDestroy() {
        vd?.release(); reader?.close(); projection?.stop(); super.onDestroy()
    }
}
