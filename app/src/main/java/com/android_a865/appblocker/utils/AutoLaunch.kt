package com.android_a865.appblocker.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import androidx.core.content.ContextCompat.startActivity


fun autoLaunchPermissionRequest(context: Context) {
    val manufacturer = Build.MANUFACTURER
    try {
        val intent = Intent()
        if ("xiaomi".equals(manufacturer, ignoreCase = true)) {
            intent.component = ComponentName(
                "com.miui.securitycenter",
                "com.miui.permcenter.autostart.AutoStartManagementActivity"
            )
        } else if ("oppo".equals(manufacturer, ignoreCase = true)) {
            intent.component = ComponentName(
                "com.coloros.safecenter",
                "com.coloros.safecenter.permission.startup.StartupAppListActivity"
            )
        } else if ("vivo".equals(manufacturer, ignoreCase = true)) {
            intent.component = ComponentName(
                "com.vivo.permissionmanager",
                "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
            )
        } else if ("Letv".equals(manufacturer, ignoreCase = true)) {
            intent.component = ComponentName(
                "com.letv.android.letvsafe",
                "com.letv.android.letvsafe.AutobootManageActivity"
            )
        } else if ("Honor".equals(manufacturer, ignoreCase = true)) {
            intent.component = ComponentName(
                "com.huawei.systemmanager",
                "com.huawei.systemmanager.optimize.process.ProtectActivity"
            )
        }
        val list: List<ResolveInfo> = context.packageManager
            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        if (list.isNotEmpty()) {
            context.startActivity(intent)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}