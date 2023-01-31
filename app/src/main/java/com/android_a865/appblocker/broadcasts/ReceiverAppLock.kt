package com.android_a865.appblocker.broadcasts

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.android_a865.appblocker.common.PreferencesManager
import com.android_a865.appblocker.utils.Utils
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ReceiverAppLock : BroadcastReceiver() {

    private fun killPackageIfRunning(context: Context, packageName: String) {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(startMain)
        activityManager.killBackgroundProcesses(packageName)
    }

    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return
        val util = Utils(context)
        val appRunning = util.getForegroundApp(context)
        val allowedApps = PreferencesManager.getAllowedApps(context)
        //val lockedApps = PreferencesManager.getLockedApps(context)
        val endTime = PreferencesManager.getEndTime(context)


        if (System.currentTimeMillis() < endTime && appRunning != "") {
            // The App is not allowed
            if (!allowedApps.contains(appRunning)) {

                Log.d("running app", appRunning)


                Thread.sleep(2000)

                killPackageIfRunning(context, appRunning)
                Toast.makeText(
                    context,
                    "App Blocker: you are blocked",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }

    }
}