package com.android_a865.appblocker.broadcasts

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.android_a865.appblocker.services.BackgroundManager

class RestartServiceWhenStopped: BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        /*Log.d("app_running", "after reboot")
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent?.action, ignoreCase = true)) {
            if (context == null) return
            BackgroundManager.instance?.startService(context)
        }
        val intent = Intent(context, )*/
    }
}