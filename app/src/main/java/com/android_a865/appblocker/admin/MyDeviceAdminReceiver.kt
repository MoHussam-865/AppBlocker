package com.android_a865.appblocker.admin

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import com.android_a865.appblocker.utils.getForegroundApp
import com.android_a865.appblocker.utils.killPackageIfRunning

class MyDeviceAdminReceiver: DeviceAdminReceiver() {

    override fun onDisableRequested(context: Context, intent: Intent): CharSequence? {

        return super.onDisableRequested(context, intent)
    }
}