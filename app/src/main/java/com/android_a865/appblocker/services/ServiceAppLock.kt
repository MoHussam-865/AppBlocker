package com.android_a865.appblocker.services

import android.app.IntentService
import android.content.Intent
import com.android_a865.appblocker.broadcasts.ReceiverAppLock
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ServiceAppLock : IntentService("ServiceAppLock") {

    @OptIn(DelicateCoroutinesApi::class)
    private fun runAppLock() {
        val endTime = System.currentTimeMillis() + 210
        while (System.currentTimeMillis() < endTime) {
            synchronized(this) {
                try {
                    val intent = Intent(this, ReceiverAppLock::class.java)
                    sendBroadcast(intent)

                    GlobalScope.launch {
                        delay(endTime - System.currentTimeMillis())
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
        BackgroundManager(this).startService()
        BackgroundManager(this).startAlarmManager()
        super.onTaskRemoved(rootIntent)
    }

    @Deprecated("Deprecated in Java")
    override fun onHandleIntent(intent: Intent?) { }

    @Deprecated("Deprecated in Java")
    override fun onDestroy() {
        BackgroundManager(this).startService()
        BackgroundManager(this).startAlarmManager()
        super.onDestroy()
    }
}