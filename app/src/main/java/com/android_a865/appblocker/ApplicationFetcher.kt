package com.android_a865.appblocker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.ResolveInfo
import java.util.*


class AppFetcher {

    companion object {
        private const val HOUR_RANGE = 1000 * 3600 * 24

        @SuppressLint("QueryPermissionsNeeded")
        fun getInstalledApplications(
            context: Context
        ): List<App> {
            val packageManager = context.packageManager
            val apps = packageManager?.getInstalledApplications(0)



            val myApps = ArrayList<App>()

            apps?.forEach { app ->
                if (!isSystemApplication(app)) {
                    val appName = packageManager.getApplicationLabel(app).toString()
                    val appIcon = packageManager.getApplicationIcon(app)
                    val appPackage = app.packageName

                    myApps.add(App(appIcon,appName,appPackage))
                }
            }

            return myApps
        }

        private fun isSystemApplication(applicationInfo: ApplicationInfo): Boolean {
            if ((applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0)
                return true
            return false
        }
    }

}

