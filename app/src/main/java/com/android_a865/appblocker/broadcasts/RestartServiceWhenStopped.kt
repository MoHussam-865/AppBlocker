package com.android_a865.appblocker.broadcasts

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.android_a865.appblocker.common.PreferencesManager
import com.android_a865.appblocker.services.BackgroundManager

class RestartServiceWhenStopped: BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UnsafeProtectedBroadcastReceiver")

    // is triggered by Alarm
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return

        val isActive = PreferencesManager.isActive(context)

        // start the service after reboot
        if (
            Intent.ACTION_BOOT_COMPLETED
                .equals(intent?.action, ignoreCase = true) &&
                    isActive
        ) {
            BackgroundManager.instance?.startService(context)
        } else if (isActive) {
            BackgroundManager.instance?.startAlarm(context)
        }
    }
}