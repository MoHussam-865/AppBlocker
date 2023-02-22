package com.android_a865.appblocker.utils

import android.app.AppOpsManager
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Process
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import com.android_a865.appblocker.admin.MyDeviceAdminReceiver
import com.android_a865.appblocker.common.services.MyAccessibilityService


private const val TAG = "app_running"

private fun isAccessibilityPermissionOn(
    context: Context,
    serviceClass: Class<MyAccessibilityService>
): Boolean {
    //your package /   accessibility service path/class
    //val service = "${context.packageName}/${serviceClass.canonicalName}"
    val service = "${context.packageName}/${serviceClass.canonicalName}"

    val settingValue = Settings.Secure
        .getString(
            context.applicationContext.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )

    Log.v(TAG, "settings: $settingValue")

    settingValue?.let {
        val services = it.split(":")
        if (services.containsIgnoreCase(service)) {
            return true
        }
    }

    return false
}

fun isAccessibilitySettingsOn(context: Context): Boolean {
    return isAccessibilityPermissionOn(
        context,
        MyAccessibilityService::class.java
    )
}

fun getAccessibilityPermission(context: Context) {
    if (!isAccessibilitySettingsOn(context)) {
        context.startActivity(
            Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        )
    }
}

fun isPermissionsGranted(context: Context): Boolean {
    val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE)
            as AppOpsManager
    val mode = appOpsManager.checkOpNoThrow(
        AppOpsManager.OPSTR_GET_USAGE_STATS,
        Process.myUid(),
        context.packageName
    )
    if (mode != AppOpsManager.MODE_ALLOWED) {
        return false
    }


    // display over other apps
    /*if (!Settings.canDrawOverlays(this)) {
        return false
    }*/

    // admin permission
    val mDPM = context.getSystemService(Context.DEVICE_POLICY_SERVICE)
            as DevicePolicyManager
    val adminName = ComponentName(context, MyDeviceAdminReceiver::class.java)
    if (!mDPM.isAdminActive(adminName)) {
        return false
    }
    return isAccessibilitySettingsOn(context)
}

@RequiresApi(33)
fun requestPermissions(context: Context) {
    // Usage State permission needed to know the current running app
    val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = appOpsManager.checkOpNoThrow(
        AppOpsManager.OPSTR_GET_USAGE_STATS,
        Process.myUid(),
        context.packageName
    )
    if (mode != AppOpsManager.MODE_ALLOWED) {
        context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
    }


    // display over other apps
    /*if (!Settings.canDrawOverlays(this)) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        startActivityForResult(intent, 0)
    }*/

    // admin permission
    val mDPM = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    val adminName = ComponentName(context, MyDeviceAdminReceiver::class.java)
    if (!mDPM.isAdminActive(adminName)) {
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminName)
        context.startActivity(intent)
    }

    getAccessibilityPermission(context)
}
