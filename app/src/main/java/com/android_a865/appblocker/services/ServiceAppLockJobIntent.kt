package com.android_a865.appblocker.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import com.android_a865.appblocker.broadcasts.ReceiverAppLock
import com.android_a865.appblocker.common.PreferencesManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ServiceAppLockJobIntent : JobIntentService() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onHandleWork(intent: Intent) {
        startForeground(this)
        runAppLock()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onTaskRemoved(rootIntent: Intent) {
        BackgroundManager.instance?.init(this)?.startService()
        BackgroundManager.instance?.init(this)?.startAlarmManager()
        super.onTaskRemoved(rootIntent)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onDestroy() {
        BackgroundManager.instance?.init(this)?.startService()
        BackgroundManager.instance?.init(this)?.startAlarmManager()
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

    private fun startForeground(context: Context) {
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel()
            } else {
                // If earlier version channel ID is not used
                // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                ""
            }

        val notification = NotificationCompat.Builder(context, channelId )
            .setOngoing(true)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("App Blocker")
            .setContentText("Service is running")
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(101, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(): String{
        val channelId = "my_service"
        val channelName = "My Background Service"
        val channel = NotificationChannel(channelId,
            channelName, NotificationManager.IMPORTANCE_NONE)
        channel.lightColor = Color.BLUE
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(channel)
        return channelId
    }

}