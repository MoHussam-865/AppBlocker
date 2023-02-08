package com.android_a865.appblocker.utils

import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.app.*
import android.app.usage.UsageEvents
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityManager
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.android_a865.appblocker.BuildConfig
import com.android_a865.appblocker.R
import com.android_a865.appblocker.common.PreferencesManager
import com.android_a865.appblocker.models.App
import com.android_a865.appblocker.services.MyAccessibilityService
import kotlinx.coroutines.delay
import java.util.*


fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
    val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    for (serviceInfo in manager.getRunningServices(Int.MAX_VALUE)) {
        if (serviceClass.name == serviceInfo.service.className) {
            return true
        }
    }
    return false
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
fun getLauncherTopApp(context: Context) {
    /*val endTime = System.currentTimeMillis()
    val beginTime = endTime - 10000

    val usageStateManager = context.getSystemService(Context.USAGE_STATS_SERVICE)
            as UsageStatsManager


    val event: UsageEvents.Event = UsageEvents.Event()
    val usageEvents = usageStateManager.queryEvents(beginTime, endTime)

    while (usageEvents.hasNextEvent()) {
        usageEvents.getNextEvent(event)
        Log.d("app_running", event.packageName.toString())
    }*/

}


@RequiresApi(33)
fun isAccessibilitySettingsOn(context: Context, service: Class<*>): Boolean {

    val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE)
            as AccessibilityManager

    val enabledServices = am.getEnabledAccessibilityServiceList(
        AccessibilityServiceInfo.FEEDBACK_ALL_MASK
    )

    enabledServices.forEach {
        val enabledServiceInfo =  it.resolveInfo.serviceInfo

        if (
            enabledServiceInfo.packageName == context.packageName &&
                    enabledServiceInfo.name == service.name
        ) {
            return true
        }
    }
    return false
}

fun createNotificationChannel(context: Context): Notification {
    var channelId = ""
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        channelId = "my_service"
        val channelName = "My Background Service"
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_NONE
        )
        channel.lightColor = Color.BLUE
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(channel)
    }

    return NotificationCompat.Builder(context, channelId)
        .setOngoing(true)
        .setSmallIcon(R.drawable.ic_block)
        .setContentTitle("App Blocker")
        .setContentText("Service is running")
        .setCategory(Notification.CATEGORY_SERVICE)
        .build()
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
fun getForegroundApp(context: Context): String {
    var currentApp = ""
    //"usagestats"
    @SuppressLint("WrongConstant")
    val usm = context.getSystemService(Context.USAGE_STATS_SERVICE)
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

@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
fun killCurrentProcess(context: Context) {
    killPackageIfRunning(
        context,
        getForegroundApp(
            context
        )
    )
}

suspend fun isDone(context: Context): Boolean {
    val endTime = PreferencesManager.getEndTime(context)
    while (endTime > System.currentTimeMillis()) {
        delay(3000)
    }
    return true
}

fun createMessage(context: Context, title: String, msg: String) {
    AlertDialog.Builder(context)
        .setTitle(title)
        .setMessage(msg)
        .setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }.show()
}


//----------------------------------------------------------------
fun ArrayList<App>.arrange(): ArrayList<App> {
    val sortedArray = ArrayList<App>()
    sortedArray.addAll(filter { it.selected })
    sortedArray.addAll(filter { !it.selected }.sortedBy { it.name })
    return sortedArray
}

fun ArrayList<App>.selectApp(
    app: App,
    isChecked: Boolean
): ArrayList<App> {
    forEachIndexed { index, application ->
        if (application.packageName == app.packageName) {
            set(index, application.copy(selected = isChecked))
        }
    }
    return arrange()
}


fun ArrayList<App>.getSelected(
    context: Context
): ArrayList<App> {
    val packages = PreferencesManager.getLockedApps(context)
    forEach {
        if (packages.contains(it.packageName)) {
            it.selected = true
        }
    }
    return this
}


/*
fun isAccessibilitySettingsOn(context: Context): Boolean {
    var accessibilityEnabled = 0
    val service = "${context.packageName}/${MyAccessibilityService::class.java.canonicalName}"
    try {
        accessibilityEnabled = Settings.Secure.getInt(
            context.applicationContext.contentResolver,
            Settings.Secure.ACCESSIBILITY_ENABLED
        )
    } catch (e: Exception) {
    }

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

class Utils(private val context: Context) {



}
*/

