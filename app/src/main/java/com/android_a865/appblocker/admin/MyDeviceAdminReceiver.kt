package com.android_a865.appblocker.admin

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.android_a865.appblocker.common.PreferencesManager
import com.android_a865.appblocker.utils.getForegroundApp
import com.android_a865.appblocker.utils.killCurrentProcess
import com.android_a865.appblocker.utils.killPackageIfRunning

class MyDeviceAdminReceiver: DeviceAdminReceiver() {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    override fun onDisableRequested(context: Context, intent: Intent): CharSequence? {
        if (PreferencesManager.isActive(context)) {
            killCurrentProcess(context)
        }
        return super.onDisableRequested(context, intent)
    }
}