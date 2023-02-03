package com.android_a865.appblocker.services

import android.app.ActivityManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.android_a865.appblocker.broadcasts.RestartServiceWhenStopped
import com.android_a865.appblocker.common.PreferencesManager


object BackgroundManager {

    const val NOTIFICATION_ID = 1
    const val NOTIFICATION_CHANNEL_ID = "channelId"
    private const val period = 15 * 1000
    private const val ALARM_ID = 159874

    private fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (serviceInfo in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == serviceInfo.service.className) {
                return true
            }
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun startService(context: Context) {
        if (PreferencesManager.isActive(context)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!isServiceRunning(context, ServiceAppLockJobIntent::class.java)) {
                    val intent = Intent(context, ServiceAppLockJobIntent::class.java)
                    context.startForegroundService(intent)
                    //ServiceAppLockJobIntent.enqueueWork(context, intent)
                }
            } else {
                if (!isServiceRunning(context, ServiceAppLock::class.java)) {
                    context.startService(Intent(context, ServiceAppLock::class.java))
                }
            }
            startAlarmManager(context)
            Log.d("app_running", "service started")
        }
        else stopService(context)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun stopService(context: Context) {

        val serviceClass = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ServiceAppLockJobIntent::class.java
        } else ServiceAppLock::class.java


        if (isServiceRunning(context, serviceClass)) {
            context.stopService(Intent(context, serviceClass))
            stopAlarm(context)
            Log.d("app_running", "service stopped")
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun startAlarmManager(context: Context) {
        val intent = Intent(context, RestartServiceWhenStopped::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_ID,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager[AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + period] =
            pendingIntent
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun stopAlarm(context: Context) {
        val intent = Intent(context, RestartServiceWhenStopped::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_ID,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.cancel(pendingIntent)
    }

    var instance: BackgroundManager? = null
        get() {
            if (field == null) field = BackgroundManager
            return field
        }
        private set

}