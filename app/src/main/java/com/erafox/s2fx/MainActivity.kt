package com.erafox.s2fx
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import rikka.shizuku.Shizuku
class MainActivity : ComponentActivity() {
    private val projectionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
        if (res.resultCode == RESULT_OK && res.data != null) {
            val svc = Intent(this, ScreenCaptureService::class.java).apply {
                action = ScreenCaptureService.ACTION_START
                putExtra(ScreenCaptureService.EXTRA_RESULT_CODE, res.resultCode)
                putExtra(ScreenCaptureService.EXTRA_DATA, res.data)
            }
            if (Build.VERSION.SDK_INT >= 26) startForegroundService(svc) else startService(svc)
        }
    }
    private val shizukuPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted -> if (granted) ShizukuInput.ensureUserService(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ll = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(48, 96, 48, 48) }
        fun btn(text: String, action: () -> Unit) { ll.addView(Button(this).apply { this.text = text; setOnClickListener { action() } }) }
        btn("1. Overlay permission") { startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:\$packageName"))) }
        btn("2. Shizuku permission") { if (Shizuku.isPreV11() || Shizuku.checkSelfPermission() == 0) Shizuku.requestPermission(0) else shizukuPermission.launch("android.permission.INTERACT_ACROSS_USERS") }
        btn("3. Start screen capture") { projectionLauncher.launch(getSystemService(MediaProjectionManager::class.java).createScreenCaptureIntent()) }
        btn("4. Start overlay") { startService(Intent(this, OverlayService::class.java)) }
        btn("5. Stop all") { stopService(Intent(this, OverlayService::class.java)); stopService(Intent(this, ScreenCaptureService::class.java)) }
        setContentView(ll)
    }
}
