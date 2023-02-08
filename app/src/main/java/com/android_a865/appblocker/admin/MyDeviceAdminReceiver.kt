package com.android_a865.appblocker.admin

import android.annotation.SuppressLint
import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.UserHandle
import android.util.Log
import androidx.annotation.RequiresApi
import com.android_a865.appblocker.common.PreferencesManager
import com.android_a865.appblocker.utils.getForegroundApp
import com.android_a865.appblocker.utils.killCurrentProcess
import com.android_a865.appblocker.utils.killPackageIfRunning

class MyDeviceAdminReceiver: DeviceAdminReceiver() {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    override fun onDisableRequested(context: Context, intent: Intent): CharSequence? {
        Log.d("app_running", "disable requested")
        if (PreferencesManager.isActive(context)) {
            killCurrentProcess(context)
            return "sorry"
        }
        return super.onDisableRequested(context, intent)
    }

}