package com.android_a865.appblocker.services

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import androidx.annotation.RequiresApi
import com.android_a865.appblocker.admin.MyDeviceAdminReceiver

class Temp: Service() {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    override fun onBind(intent: Intent?): IBinder? {
        val admin = MyDeviceAdminReceiver()

        admin.onDisableRequested(this, Intent())

        val manager = admin.getManager(this)



        val y = "com.android.settings"

        val x = Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS





        return null
    }


}