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
import android.provider.Settings
import android.text.TextUtils.SimpleStringSplitter
import android.util.Log
import androidx.annotation.RequiresApi
import com.android_a865.appblocker.services.MyAccessibilityService
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

fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
    val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    for (serviceInfo in manager.getRunningServices(Int.MAX_VALUE)) {
        if (serviceClass.name == serviceInfo.service.className) {
            return true
        }
    }
    return false
}


@RequiresApi(Build.VERSION_CODES.O)
fun createNotificationChannel(context: Context): String {
    val channelId = "my_service"
    val channelName = "My Background Service"
    val channel = NotificationChannel(
        channelId,
        channelName,
        NotificationManager.IMPORTANCE_DEFAULT
    )
    channel.lightColor = Color.BLUE
    channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
    val service = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    service.createNotificationChannel(channel)
    return channelId
}


@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
fun getForegroundApp(context: Context): String {
    var currentApp = ""
    //"usagestats"
    @SuppressLint("WrongConstant")
    val usm = context.getSystemService("usagestats")
            as UsageStatsManager?
    assert(usm != null)

    val time = System.currentTimeMillis()

    val appList = usm!!.queryUsageStats(
        UsageStatsManager.INTERVAL_DAILY,
        time - 1000 * 1000,
        time
    )
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
        if (!tasks.isNullOrEmpty()) {
            currentApp = tasks[0].processName
        }
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


fun isAccessibilitySettingsOn(context: Context): Boolean {
    var accessibilityEnabled = 0
    val service = "${context.packageName}/${MyAccessibilityService::class.java.canonicalName}"
    try {
        accessibilityEnabled = Settings.Secure.getInt(
            context.applicationContext.contentResolver,
            Settings.Secure.ACCESSIBILITY_ENABLED
        )
    } catch (e: Exception) {  }

    val mStringColonSplitter = SimpleStringSplitter(':')
    if (accessibilityEnabled == 1) {
        val settingValue: String = Settings.Secure.getString(
            context.applicationContext.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        mStringColonSplitter.setString(settingValue)
        while (mStringColonSplitter.hasNext()) {
            val accessibilityService = mStringColonSplitter.next()

            if (accessibilityService.equals(service, ignoreCase = true)) {
                return true
            }
        }
    }
    return false
}