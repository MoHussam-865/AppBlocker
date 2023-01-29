package com.android_a865.appblocker.services

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import com.android_a865.appblocker.broadcasts.ReceiverAppLock
import kotlinx.coroutines.*


class ServiceAppLockJobIntent : JobIntentService() {
    override fun onHandleWork(intent: Intent) {
        runAppLock()
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        BackgroundManager(this).startService()
        BackgroundManager(this).startAlarmManager()
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        BackgroundManager(this).startService()
        BackgroundManager(this).startAlarmManager()
        super.onDestroy()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun runAppLock() {
        val endTime = System.currentTimeMillis() + 210
        while (System.currentTimeMillis() < endTime) {
            synchronized(this) {
                try {
                    val intent = Intent(
                        this,
                        ReceiverAppLock::class.java)
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

    companion object {
        private const val JOB_ID = 15462
        fun enqueueWork(context: Context?, work: Intent?) {
            context?.let {
                enqueueWork(
                    it,
                    ServiceAppLockJobIntent::class.java,
                    JOB_ID,
                    work!!
                )
            }

        }
    }
}