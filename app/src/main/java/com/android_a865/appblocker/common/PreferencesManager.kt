package com.android_a865.appblocker.common

import android.content.Context
import android.preference.PreferenceManager
import android.widget.Toast
import com.android_a865.appblocker.models.App
import com.android_a865.appblocker.services.BackgroundManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object PreferencesManager {
    private const val LOCKED_APPS = "locked_apps"
    private const val ALLOWED_APPS = "allowed_apps"
    private const val END_TIME = "end_time"
    private const val LAST_TIME = "last_time"


    fun getLockedApps(context: Context): List<String> {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(LOCKED_APPS, "")?.split("/")
            ?.filter { it != "" } ?: emptyList()
    }

    fun getAllowedApps(context: Context): List<String> {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(ALLOWED_APPS, "")?.split("/")
            ?.filter { it != "" } ?: emptyList()
    }

    fun getEndTime(context: Context) = PreferenceManager
        .getDefaultSharedPreferences(context)
        .getLong(END_TIME, System.currentTimeMillis())


    fun getLastTime(context: Context) = PreferenceManager
        .getDefaultSharedPreferences(context)
        .getInt(LAST_TIME, 0)


    fun setLockedApps(context: Context, apps: List<App>) {
        var data = ""
        apps.forEach {
            data += it.packageName + "/"
        }

        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(LOCKED_APPS, data)
            .apply()
    }

    fun setupLockSettings(
        context: Context,
        apps: List<App>,
        lastTime: Int
    ) {

        val endTime = System.currentTimeMillis() + lastTime * 60000

        if (endTime < getEndTime(context)) return

        val allApps = AppFetcher.getAllInstalledApplications(context)

        val blockedApps = apps.filter { it.selected }
            .joinToString("/") { it.packageName }

        val allowedApps = allApps.filter {
            it.packageName !in blockedApps
        }.joinToString("/") { it.packageName }


        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(LOCKED_APPS, blockedApps)
            .putString(ALLOWED_APPS, allowedApps)
            .putLong(END_TIME, endTime)
            .putInt(LAST_TIME, lastTime)
            .apply()
    }

    fun isActive(context: Context): Boolean {
        val lastTime = PreferenceManager.getDefaultSharedPreferences(context)
            .getLong(END_TIME, 0)
        return lastTime > System.currentTimeMillis()
    }


    /*
    fun setEndTime(context: Context, time: Long) {
        PreferenceManager
            .getDefaultSharedPreferences(context)
            .edit()
            .putLong(END_TIME, time)
            .apply()
    }

    fun setAllowedApps(context: Context, apps: List<App>) {
        var data = ""
        apps.forEach {
            data += it.packageName + "/"
        }

        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(ALLOWED_APPS, data)
            .apply()
    }

    fun setLastTime(context: Context, time: Int) {
        PreferenceManager
            .getDefaultSharedPreferences(context)
            .edit()
            .putInt(LAST_TIME, time)
            .apply()
    }

     */

}

