package com.android_a865.appblocker.common

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import com.android_a865.appblocker.models.App
import kotlin.collections.ArrayList


object AppFetcher {

    /*
    @SuppressLint("QueryPermissionsNeeded")
    fun getInstalledApplications(context: Context): List<App> {
        val packageManager = context.packageManager
        val apps = packageManager?.getInstalledApplications(0)


        val myApps = ArrayList<App>()

        apps?.forEach { app ->
            if (!isSystemApplication(app)) {
                val appName = packageManager.getApplicationLabel(app).toString()
                val appIcon = packageManager.getApplicationIcon(app)
                val appPackage = app.packageName

                myApps.add(App(appIcon, appName, appPackage))
            }
        }

        return myApps
    }

    private fun isSystemApplication(applicationInfo: ApplicationInfo): Boolean {
        if ((applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0)
            return true
        return false
    }

    */

    @SuppressLint("QueryPermissionsNeeded")
    fun getAllInstalledApplications(context: Context): List<App> {
        val packageManager = context.packageManager
        val apps = packageManager?.getInstalledApplications(0)
        val myApps = ArrayList<App>()
        apps?.forEach { app ->
            val appName = packageManager.getApplicationLabel(app).toString()
            val appIcon = packageManager.getApplicationIcon(app)
            val appPackage = app.packageName
            myApps.add(App(appIcon, appName, appPackage))
        }
        return myApps
    }

    fun getApps(context: Context): List<App> {
        val apps = ArrayList<App>()
        val packageManager = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val resolveInfoList = packageManager.queryIntentActivities(intent, 0)

        resolveInfoList.forEach { resolveInfo ->
            val activityInfo = resolveInfo.activityInfo

            val name = activityInfo.loadLabel(context.packageManager).toString()
            val icon = activityInfo.loadIcon(context.packageManager)
            val packageName = activityInfo.packageName

            if (!(packageName.contains("com.android_a865.appblocker") ||
                        packageName.contains("com.android.settings"))
            ) apps.add(App(icon, name, packageName))

        }

        return apps
    }
}



