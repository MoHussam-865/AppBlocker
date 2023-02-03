package com.android_a865.appblocker.utils

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import java.util.*


/*class Utils(private val context: Context) {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    fun getLauncherTopApp(): String {
        val endTime = System.currentTimeMillis()
        val beginTime = endTime - 10000
        var result = ""

        val usageStateManager = context.getSystemService(Context.USAGE_STATS_SERVICE)
                as UsageStatsManager

        val event: UsageEvents.Event = UsageEvents.Event()
        val usageEvents = usageStateManager.queryEvents(beginTime, endTime)
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                result = event.packageName
            }
        }

        return result
    }

}
*/

fun getForegroundApp(context: Context): String {
    var currentApp = ""
    @SuppressLint("WrongConstant") val usm =
        context.getSystemService("usagestats") as UsageStatsManager?
    val time = System.currentTimeMillis()
    assert(usm != null)
    val appList =
        usm!!.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time)
    if (appList != null && appList.size > 0) {
        val mySortedMap: SortedMap<Long, UsageStats> = TreeMap()
        for (usageStats in appList) {
            mySortedMap[usageStats.lastTimeUsed] = usageStats
        }
        if (!mySortedMap.isEmpty()) {
            currentApp = mySortedMap[mySortedMap.lastKey()]?.packageName ?: ""
        }
    } else {
        val am = (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?)!!
        val tasks = am.runningAppProcesses
        currentApp = tasks[0].processName
    }

    Log.d("running Foreground", "Current App in foreground is: $currentApp")
    return currentApp


}

fun killPackageIfRunning(context: Context, packageName: String) {
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val startMain = Intent(Intent.ACTION_MAIN)
    startMain.addCategory(Intent.CATEGORY_HOME)
    startMain.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
    context.startActivity(startMain)
    activityManager.killBackgroundProcesses(packageName)
}