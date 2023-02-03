package com.android_a865.appblocker.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.android_a865.appblocker.common.PreferencesManager
import com.android_a865.appblocker.services.BackgroundManager
import com.android_a865.appblocker.utils.getForegroundApp
import com.android_a865.appblocker.utils.killPackageIfRunning

class ReceiverAppLock : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return
        val appRunning = getForegroundApp(context)
        val allowedApps = PreferencesManager.getAllowedApps(context)
        //val lockedApps = PreferencesManager.getLockedApps(context)
        val isActive = PreferencesManager.isActive(context)


        if (isActive && appRunning != "") {
            // The App is not allowed
            if (!allowedApps.contains(appRunning)) {

                Thread.sleep(1000)
                killPackageIfRunning(context, appRunning)
                Toast.makeText(
                    context,
                    "App Blocker: you are blocked",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("app_running", "service is running")
            }
        } else {
            BackgroundManager.instance?.stopService(context)
        }

    }
}