package com.android_a865.appblocker.common.broadcasts

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.android_a865.appblocker.common.PreferencesManager
import com.android_a865.appblocker.common.services.MyAccessibilityService
import com.android_a865.appblocker.utils.*

class ReceiverAppLock : BroadcastReceiver() {

    private val TAG = "app_running"

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return
        handleAppsBlocking(context)

        handleUninstallationBlocking(context)

    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private fun handleAppsBlocking(context: Context) {
        val appRunning = getForegroundApp(context)
        val allowedApps = PreferencesManager.getAllowedApps(context)
        //val lockedApps = PreferencesManager.getLockedApps(context)
        //Log.d(TAG, appRunning)

        if (appRunning != "" && !allowedApps.contains(appRunning)) {

            Thread.sleep(1000)
            killPackageIfRunning(context, appRunning)
            Toast.makeText(
                context,
                "App Blocker: you are blocked",
                Toast.LENGTH_SHORT
            ).show()
            Log.d("app_running", "app blocked")
            Thread.sleep(3000)

        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private fun handleUninstallationBlocking(context: Context) {
        val appRunning = getForegroundApp(context)
        val isRunning = isServiceRunning(context, MyAccessibilityService::class.java)
        //Log.d(TAG, "access running: $isRunning")
        // if it's not running it can be :-
        // because the permission is not enabled then block settings
        // it can just needs to be run
        if (!isRunning) {
            // make the user turn it on or block settings

            if (!isAccessibilitySettingsOn(context)) {

                Log.d(TAG, "block setting")
                // block settings
                if (appRunning == "com.android.settings") {
                    killPackageIfRunning(context, appRunning)
                    accessibilityRequestMessage(context)
                }

            } else {
                // the service just needs to be started
                context.startService(
                    Intent(
                        context,
                        MyAccessibilityService::class.java
                    )
                )
            }

        }

        // if it's running it will handle the uninstallation protection

    }

}