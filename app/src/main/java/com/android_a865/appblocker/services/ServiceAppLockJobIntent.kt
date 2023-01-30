package com.android_a865.appblocker.services

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import com.android_a865.appblocker.MainActivity
import com.android_a865.appblocker.R
import com.android_a865.appblocker.broadcasts.ReceiverAppLock
import com.android_a865.appblocker.common.PreferencesManager
import kotlinx.coroutines.*


class ServiceAppLockJobIntent : JobIntentService() {
    override fun onHandleWork(intent: Intent) {
        runAppLock()
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        BackgroundManager.instance?.init(this)?.startService()
        BackgroundManager.instance?.init(this)?.startAlarmManager()
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        BackgroundManager().init(this).startService()
        BackgroundManager().init(this).startAlarmManager()
        super.onDestroy()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun runAppLock() {
        val endTime = PreferencesManager.getEndTime(this)
        while (System.currentTimeMillis() < endTime) {
            synchronized(this) {
                try {
                    val intent = Intent(this, ReceiverAppLock::class.java)
                    sendBroadcast(intent)
                    Log.d("running error","runAppLock in ServiceAppLockJob")
                    GlobalScope.launch {
                        delay(1000)
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


    private fun startForeground() {
        val notificationIntent = Intent(
            this,
            MainActivity::class.java
        )
        val pendingIntent = PendingIntent.getActivity(
            this,0,notificationIntent, 0
        )
        startForeground(
            BackgroundManager.NOTIFICATION_ID,
            NotificationCompat.Builder(
                this,
                BackgroundManager.NOTIFICATION_CHANNEL_ID
            ).setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Service running")
                .setContentIntent(pendingIntent)
                .build()
        )
    }

}