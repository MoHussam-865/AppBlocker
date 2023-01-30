package com.android_a865.appblocker.services

import android.app.ActivityManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.android_a865.appblocker.MainActivity
import com.android_a865.appblocker.R
import com.android_a865.appblocker.broadcasts.RestartServiceWhenStopped



class BackgroundManager {

    lateinit var context: Context

    fun init(ctx: Context): BackgroundManager {
        context = ctx
        return this
    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (serviceInfo in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == serviceInfo.service.className) {
                return true
            }
        }
        return false
    }

    fun startService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!isServiceRunning(ServiceAppLockJobIntent::class.java)) {
                val intent = Intent(context, ServiceAppLockJobIntent::class.java)
                ServiceAppLockJobIntent.enqueueWork(context, intent)
            }
        } else {
            if (!isServiceRunning(ServiceAppLock::class.java)) {
                context.startService(Intent(context, ServiceAppLock::class.java))
            }
        }
    }

    fun stopService(serviceClass: Class<*>) {
        if (isServiceRunning(serviceClass)) {
            context.stopService(Intent(context, serviceClass))
        }
    }

    fun startAlarmManager() {
        val intent = Intent(context, RestartServiceWhenStopped::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, ALARM_ID, intent, 0)
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager[AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + period] =
            pendingIntent
    }

    fun stopAlarm() {
        val intent = Intent(context, RestartServiceWhenStopped::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, ALARM_ID, intent, 0)
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.cancel(pendingIntent)
    }

    companion object {
        private const val period = 15 * 1000
        private const val ALARM_ID = 159874
        const val NOTIFICATION_ID = 1
        const val NOTIFICATION_CHANNEL_ID = "channelId"

        var instance: BackgroundManager? = null
            get() {
                if (field == null) {
                    field = BackgroundManager()
                }
                return field
            }
            private set

    }

}