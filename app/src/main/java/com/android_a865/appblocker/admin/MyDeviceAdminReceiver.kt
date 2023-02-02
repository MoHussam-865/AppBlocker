package com.android_a865.appblocker.admin

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import com.android_a865.appblocker.utils.Utils

class MyDeviceAdminReceiver: DeviceAdminReceiver() {

    override fun onDisableRequested(context: Context, intent: Intent): CharSequence? {
        val runningApp = Utils(context).getForegroundApp()

        return super.onDisableRequested(context, intent)
    }
}