package com.android_a865.appblocker.common.services

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.JobIntentService
import com.android_a865.appblocker.admin.MyDeviceAdminReceiver
import com.android_a865.appblocker.common.broadcasts.ReceiverAppLock
import com.android_a865.appblocker.common.PreferencesManager
import com.android_a865.appblocker.utils.createNotificationChannel


class ServiceAppLockJobIntent : JobIntentService() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onHandleWork(intent: Intent) {
        startForeground(101, createNotificationChannel(this))
        runAppLock()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onTaskRemoved(rootIntent: Intent) {
        BackgroundManager.startService(this)
        super.onTaskRemoved(rootIntent)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onDestroy() {
        BackgroundManager.startService(this)
        super.onDestroy()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun runAppLock() {
        val endTime = PreferencesManager.getEndTime(this)

        while (System.currentTimeMillis() < endTime) {
            synchronized(this) {
                try {
                    val intent = Intent(this, ReceiverAppLock::class.java)
                    sendBroadcast(intent)

                    // this is important to not freeze the UI
                    Thread.sleep(1500)

                    //Log.d("app_running", "is Active")

                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }

        // stop the service after finish
        BackgroundManager.stopService(this)
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