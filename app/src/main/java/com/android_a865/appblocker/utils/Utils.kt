package com.android_a865.appblocker.utils

import android.app.ActivityManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import android.text.TextUtils
import androidx.annotation.RequiresApi

class Utils(private val context: Context) {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    fun getLauncherTopApp(): String {
        val endTime = System.currentTimeMillis()
        val beginTime = endTime - 10000
        val result = ""

        val manager = context.getSystemService(Context.ACTIVITY_SERVICE)
                as ActivityManager
        val usageStateManager = context.getSystemService(Context.USAGE_STATS_SERVICE)
                as UsageStatsManager

        val event: UsageEvents.Event = UsageEvents.Event()
        val usageEvents = usageStateManager.queryEvents(beginTime, endTime)
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                val result = event.packageName
            }
        }

        return result
    }

}