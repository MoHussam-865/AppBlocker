package com.android_a865.appblocker.broadcasts

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.android_a865.appblocker.MainActivity
import com.android_a865.appblocker.services.PreferencesManager
import com.android_a865.appblocker.utils.Utils

class ReceiverAppLock: BroadcastReceiver() {

    private fun killPackageIfRunning(context: Context, packageName: String) {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE)
                as ActivityManager
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        context.startActivity(startMain)
        activityManager.killBackgroundProcesses(packageName)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return
        val util = Utils(context)
        val appRunning = util.getLauncherTopApp()
        val allowedApps = PreferencesManager.getAllowedApps(context)
        val endTime = PreferencesManager.getEndTime(context)

        if (System.currentTimeMillis() < endTime) {
            if (appRunning !in allowedApps) {

                killPackageIfRunning(context, appRunning)

                val i = Intent(context, MainActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                i.putExtra("broadcast_receiver", "broadcast_receiver")
                context.startActivity(i)

                Toast.makeText(
                    context,
                    "App Blocker: you are blocked",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }
}