package com.android_a865.appblocker.broadcasts

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.android_a865.appblocker.services.BackgroundManager

class RestartServiceWhenStopped: BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("UnsafeProtectedBroadcastReceiver")

    // is triggered by Alarm
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return
        BackgroundManager.instance?.startService(context)

    }
}