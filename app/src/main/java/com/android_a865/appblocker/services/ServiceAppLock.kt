package com.android_a865.appblocker.services

import android.app.IntentService
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.android_a865.appblocker.MainActivity
import com.android_a865.appblocker.R
import com.android_a865.appblocker.broadcasts.ReceiverAppLock
import com.android_a865.appblocker.common.PreferencesManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ServiceAppLock : IntentService("ServiceAppLock") {



    @OptIn(DelicateCoroutinesApi::class)
    private fun runAppLock() {
        val endTime = PreferencesManager.getEndTime(this)
        while (System.currentTimeMillis() < endTime) {
            synchronized(this) {
                try {
                    val intent = Intent(this, ReceiverAppLock::class.java)
                    sendBroadcast(intent)
                    Log.d("running error","runAppLock in ServiceAppLock")
                    GlobalScope.launch {
                        delay(15000)
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        runAppLock()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        BackgroundManager().init(this).startService()
        BackgroundManager().init(this).startAlarmManager()
        super.onTaskRemoved(rootIntent)
    }

    @Deprecated("Deprecated in Java")
    override fun onHandleIntent(intent: Intent?) { }

    @Deprecated("Deprecated in Java")
    override fun onDestroy() {
        BackgroundManager().init(this).startService()
        BackgroundManager().init(this).startAlarmManager()
        super.onDestroy()
    }
}