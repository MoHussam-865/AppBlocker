package com.android_a865.appblocker.common.services

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.android_a865.appblocker.common.PreferencesManager
import com.android_a865.appblocker.common.broadcasts.RestartServiceWhenStopped
import com.android_a865.appblocker.utils.isServiceRunning

/**
 * Handles starting and stoping services
 */
object BackgroundManager {

    private var accessibilityService: MyAccessibilityService? = null
    private const val period = 15 * 1000
    private const val ALARM_ID = 159874


    @RequiresApi(Build.VERSION_CODES.S)
    fun startService(context: Context) {
        if (PreferencesManager.isActive(context)) {
            if (!isServiceRunning(context, ServiceAppLockJobIntent::class.java)) {
                val intent = Intent(context, ServiceAppLockJobIntent::class.java)
                ServiceAppLockJobIntent.enqueueWork(context, intent)
                Log.d("app_running", "service started")
            }
            accessibility()
            startAlarm(context)
        } else stopService(context)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun stopService(context: Context) {
        val serviceClass = ServiceAppLockJobIntent::class.java
        if (isServiceRunning(context, serviceClass)) {
            context.stopService(Intent(context, serviceClass))
            stopAlarm(context)
            Log.d("app_running", "service stopped")
        }
        accessibilityService?.onDestroy()
    }

    @SuppressLint("ScheduleExactAlarm")
    @RequiresApi(Build.VERSION_CODES.S)
    fun startAlarm(context: Context) {
        val intent = Intent(context, RestartServiceWhenStopped::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_ID,
            intent,
            PendingIntent.FLAG_MUTABLE
        )
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        manager.setExact(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + period,
            pendingIntent
        )
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


    private fun accessibility() {
        if (
            accessibilityService == null
        ) {
            accessibilityService = MyAccessibilityService()
        }
    }


}